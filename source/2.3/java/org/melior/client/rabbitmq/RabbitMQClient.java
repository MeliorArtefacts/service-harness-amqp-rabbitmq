/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.rabbitmq;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import org.melior.client.exception.RemotingException;
import org.melior.context.transaction.TransactionContext;
import org.melior.logging.core.Logger;
import org.melior.logging.core.LoggerFactory;
import org.melior.service.exception.ExceptionType;
import org.melior.util.object.ObjectUtil;
import org.melior.util.time.Timer;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implements an easy to use, auto-configuring RabbitMQ client with connection
 * pooling, configurable backoff strategy and automatic object mapping.
 * <p>
 * The client writes timing details to the logs while dispatching AMQP messages
 * to the RabbitMQ server.  The client automatically converts any exception that
 * occurs during communication with the RabbitMQ server into a standard
 * {@code RemotingException}.
 * @author Melior
 * @since 2.3
 */
public class RabbitMQClient extends RabbitMQClientConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean async;

    private boolean ssl;

    private SSLContext sslContext;

    private ObjectMapper objectMapper;

    private ConnectionManager connectionManager;

    private RabbitTemplate rabbitMQTemplate;

    /**
     * Constructor.
     * @param async The asynchronous transport indicator
     * @param ssl The SSL indicator
     * @param sslContext The SSL context
     */
    RabbitMQClient(
        final boolean async,
        final boolean ssl,
        final SSLContext sslContext) {

        super();

        this.async = async;

        this.ssl = ssl;

        this.sslContext = sslContext;
    }

    /**
     * Configure client.
     * @param clientConfig The new client configuration parameters
     * @return The RabbitMQ client
     */
    public RabbitMQClient configure(
        final RabbitMQClientConfig clientConfig) {
        super.configure(clientConfig);

        return this;
    }

    /**
     * Initialize client.
     * @throws RemotingException if unable to initialize the client
     */
    private void initialize() throws RemotingException {

        if (rabbitMQTemplate != null) {
            return;
        }

        if (StringUtils.hasLength(getUrl()) == false) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "URL must be configured.");
        }

        if (StringUtils.hasLength(getUsername()) == false) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "User name must be configured.");
        }

        if (StringUtils.hasLength(getPassword()) == false) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "Password must be configured.");
        }

        if ((StringUtils.hasLength(getRoutingKey()) == false)
            && (StringUtils.hasLength(getQueue()) == false)) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "Routing key or queue must be configured.");
        }

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        connectionManager = new ConnectionManager(this, new ConnectionFactory(async, this));

        rabbitMQTemplate = new RabbitTemplate();
        rabbitMQTemplate.setExchange((getExchange() == null) ? rabbitMQTemplate.getExchange() : getExchange());
        rabbitMQTemplate.setConnectionFactory(connectionManager);
        rabbitMQTemplate.setReceiveTimeout(getRequestTimeout());
    }

    /**
     * Get connection factory.
     * @return The connection factory
     * @throws RemotingException if unable to get the connection factory
     */
    public org.springframework.amqp.rabbit.connection.ConnectionFactory getConnectionFactory() throws RemotingException {

        initialize();

        return connectionManager;
    }

    /**
     * Send message.
     * @param <Rq> The request type
     * @param message The message object
     * @throws RemotingException if unable to send the message
     */
    public <Rq> void send(
        final Rq message) throws RemotingException {

        send(message, Void.class);
    }

    /**
     * Send message and receive response.
     * @param <Rq> The request type
     * @param <Rs> The response type
     * @param message The message object
     * @param responseType The response object type
     * @return The response object
     * @throws RemotingException if unable to send the message
     */
    public <Rq, Rs> Rs send(
        final Rq message,
        final Class<Rs> responseType) throws RemotingException {

        String methodName = "send";
        String payload;
        TransactionContext transactionContext;
        Timer timer;
        Object reply;
        long duration;
        Rs response;

        initialize();

        try {

            payload = (message instanceof String) ? (String) message : objectMapper.writeValueAsString(message);
        }
        catch (Exception exception) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "Failed to serialize message: " + exception.getMessage(), exception);
        }

        if (payload != null) {
            logger.debug(methodName, "message = ", payload);
        }

        transactionContext = TransactionContext.get();

        timer = Timer.ofNanos().start();

        try {

            if (responseType != Void.class) {

                reply = rabbitMQTemplate.convertSendAndReceive(ObjectUtil.coalesce(getRoutingKey(), getQueue()), (Object) payload,
                    new MessagePostProcessor(transactionContext.getTransactionId()));

                payload = (reply == null) ? null : (String) reply;
            }
            else {

                rabbitMQTemplate.convertAndSend(ObjectUtil.coalesce(getRoutingKey(), getQueue()), (Object) payload,
                    new MessagePostProcessor((transactionContext.getTransactionId())));

                payload = null;
            }

            duration = timer.elapsedTime(TimeUnit.MILLISECONDS);

            logger.debug(methodName, "Message sent successfully.  Duration = ", duration, " ms.");

            if (payload != null) {
                logger.debug(methodName, "response = ", payload);
            }

        }
        catch (AmqpException exception) {

            duration = timer.elapsedTime(TimeUnit.MILLISECONDS);

            logger.debug(methodName, "Message send failed.  Duration = ", duration, " ms.");

            throw new RemotingException(ExceptionType.REMOTING_COMMUNICATION, exception.getMessage());
        }
        catch (Exception exception) {
            throw new RemotingException(ExceptionType.REMOTING_COMMUNICATION, "Failed to send message: " + exception.getMessage(), exception);
        }

        try {

            response = (payload == null) ? null : (responseType == String.class) ? responseType.cast(payload) : objectMapper.readValue(payload, responseType);
        }
        catch (Exception exception) {
            throw new RemotingException(ExceptionType.LOCAL_APPLICATION, "Failed to deserialize response: " + exception.getMessage(), exception);
        }

        return response;
    }

}
