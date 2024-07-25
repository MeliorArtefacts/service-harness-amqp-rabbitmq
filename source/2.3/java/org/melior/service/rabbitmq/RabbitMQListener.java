/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.service.rabbitmq;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.melior.client.exception.RemotingException;
import org.melior.client.rabbitmq.RabbitMQClient;
import org.melior.logging.core.Logger;
import org.melior.logging.core.LoggerFactory;
import org.melior.service.core.ServiceState;
import org.melior.util.thread.DaemonThread;
import org.melior.util.thread.ThreadControl;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.util.backoff.ExponentialBackOff;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implements an easy to use, auto-configuring RabbitMQ listener which listens
 * to registered RabbitMQ queues and processes any new messages that arrive.
 * <p>
 * If a queue is configured with a {@code SingletonProcessor}, then any
 * new messages that arrive will be processed by the listener individually.
 * <p>
 * The listener may be configured with multiple threads to speed up processing.
 * @author Melior
 * @since 2.3
 * @see RabbitMQQueue
 */
public class RabbitMQListener<T> extends RabbitMQListenerConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<T> entityClass;

    private RabbitMQClient rabbitMQClient;

    private ObjectMapper objectMapper;

    private Map<String, RabbitMQQueue<T>> queueMap;

    /**
     * Constructor.
     * @param entityClass The entity class
     * @param rabbitMQClient The RabbitMQ client
     */
    RabbitMQListener(
        final Class<T> entityClass,
        final RabbitMQClient rabbitMQClient) {

        super();

        this.entityClass = entityClass;

        this.rabbitMQClient = rabbitMQClient;

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        queueMap = new HashMap<String, RabbitMQQueue<T>>();
    }

    /**
     * Register queue to listen to.  The queue is the default one as configured on the RabbitMQ client.
     * @return The queue
     */
    public RabbitMQQueue<T> registerDefault() {
        return register(rabbitMQClient.getQueue());
    }

    /**
     * Register queue to listen to.
     * @param queueName The queue name
     * @return The queue
     */
    public RabbitMQQueue<T> register(
        final String queueName) {

        RabbitMQQueue<T> queue;

        queue = queueMap.get(queueName);

        if (queue == null) {

            queue = new RabbitMQQueue<T>(this, queueName, getConsumers());

            queueMap.put(queueName, queue);
        }

        return queue;
    }

    /**
     * Register queue to listen to.  The queue is the default one as configured on the RabbitMQ client.
     * @return The queue
     */
    RabbitMQQueue<T> registerDefaultInterceptor() {
        return registerInterceptor(rabbitMQClient.getQueue());
    }

    /**
     * Register queue to listen to.
     * @param queueName The queue name
     * @return The queue
     */
    RabbitMQQueue<T> registerInterceptor(
        final String queueName) {

        RabbitMQQueue<T> queue;

        queue = queueMap.get(queueName);

        if (queue == null) {

            queue = new RabbitMQRequestInterceptor<T>(this, queueName, getConsumers());

            queueMap.put(queueName, queue);
        }

        return queue;
    }

    /**
     * Start listening to queue.
     * @param queue The queue
     * @throws RemotingException if unable to start listening to the queue
     */
    void start(
        final RabbitMQQueue<T> queue) throws RemotingException {

        DaemonThread.create(() -> listen(queue));
    }

    /**
     * Listen to queue and process new arrivals.
     * @param queue The queue
     */
    private void listen(
        final RabbitMQQueue<T> queue) {

        String methodName = "listen";
        MessageListener processor;
        SimpleMessageListenerContainer container;

        logger.debug(methodName, "Started listening to queue [", queue.getName(), "].");

        while (ServiceState.isActive() == true) {

            while (ServiceState.isSuspended() == true) {

                ThreadControl.wait(queue, 100, TimeUnit.MILLISECONDS);
            }

            while (true) {

                try {

                    processor = record -> process(queue, record);

                    container = new SimpleMessageListenerContainer();
                    container.setQueueNames(queue.getName());
                    container.setMessageListener(processor);
                    container.setConnectionFactory(rabbitMQClient.getConnectionFactory());
                    container.setMissingQueuesFatal(false);
                    container.setMaxConcurrentConsumers(getConsumers());
                    container.setConcurrentConsumers(getConsumers());
                    container.setStartConsumerMinInterval(1000);
                    container.setPrefetchCount(getPrefetch());
                    container.setReceiveTimeout(getRequestTimeout());
                    container.setRecoveryBackOff(new ExponentialBackOff(getBackoffPeriod(), getBackoffMultiplier()));
                    container.start();

                    return;
                }
                catch (Throwable exception) {
                    logger.error(methodName, "Failed to start listening to queue: ", exception.getMessage(), exception);

                    break;
                }

            }

            ThreadControl.wait(queue, getConnectionTimeout(), TimeUnit.MILLISECONDS);
        }

    }

    /**
     * Process record.
     * @param queue The queue
     * @param record The record
     * @throws RemotingException if unable to process the record
     */
    private void process(
        final RabbitMQQueue<T> queue,
        final Message message) throws RuntimeException {

        T message1;

        queue.getTotalMessages().increment();

        try {

            message1 = objectMapper.readValue(new String(message.getBody()), entityClass);

            queue.getProcessor().process(message1);
        }
        catch (Throwable exception) {

            queue.getFailedMessages().increment();

            throw new AmqpException(exception.getMessage());
        }

    }

}
