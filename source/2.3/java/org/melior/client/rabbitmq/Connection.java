/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.rabbitmq;
import java.lang.reflect.Method;
import org.melior.client.exception.RemotingException;
import org.melior.client.pool.ConnectionPool;

/**
 * Implements a wrapper around a RabbitMQ {@code Connection} delegate.  The connection
 * is pooled until it experiences a connectivity error, or until it expires, either
 * due to being in surplus at the timeout interval, or due to it reaching the maximum
 * lifetime for a connection.
 * @author Melior
 * @since 2.3
 */
public class Connection extends org.melior.client.core.Connection<RabbitMQClientConfig, Connection, org.springframework.amqp.rabbit.connection.Connection> {

    private RabbitMQConnectionFactory connectionFactory;

    private Channel channel;

    /**
     * Constructor.
     * @param configuration The client configuration
     * @param connectionPool The connection pool
     * @param connectionFactory The RabbitMQ connection factory
     * @throws RemotingException if an error occurs during the construction
     */
    public Connection(
        final RabbitMQClientConfig configuration,
        final ConnectionPool<RabbitMQClientConfig, Connection, org.springframework.amqp.rabbit.connection.Connection> connectionPool,
        final RabbitMQConnectionFactory connectionFactory) throws RemotingException {

        super(configuration, connectionPool);

        this.connectionFactory = connectionFactory;
    }

    /**
     * Open raw connection.
     * @return The raw connection
     * @throws Exception if unable to open the raw connection
     */
    protected org.springframework.amqp.rabbit.connection.Connection openConnection() throws Exception {

        return connectionFactory.createConnection();
    }

    /**
     * Close raw connection.
     * @param connection The raw connection
     * @throws Exception if unable to close the raw connection
     */
    protected void closeConnection(
        final org.springframework.amqp.rabbit.connection.Connection connection) throws Exception {

        connection.close();
    }

    /**
     * Handle proxy invocation.
     * @param object The object on which the method was invoked
     * @param method The method to invoke
     * @param args The arguments to invoke with
     * @return The result of the invocation
     * @throws Throwable if the invocation fails
     */
    public Object invoke(
        final Object object,
        final Method method,
        final Object[] args) throws Throwable {

        String methodName;
        Object invocationResult;

        methodName = method.getName();

        if (methodName.equals("createChannel") == true) {

            if (channel == null) {

                channel = new Channel(configuration, this);
                channel.setDelegate((com.rabbitmq.client.Channel) invoke(method, args));
            }

            invocationResult = channel.getProxy();
        }

        else if (methodName.equals("close") == true) {

            releaseConnection(this);

            invocationResult = null;
        }
        else {

            invocationResult = invoke(method, args);
        }

        return invocationResult;
    }

}
