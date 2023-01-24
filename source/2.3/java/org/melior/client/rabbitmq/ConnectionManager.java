/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.rabbitmq;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

/**
 * Implements a manager for persistent RabbitMQ {@code Connection} objects, for connections to
 * RabbitMQ servers. The manager writes statistics from the underlying connection pool to
 * the logs whenever a {@code Connection} is borrowed from the pool.
 * @author Melior
 * @since 2.3
 */
public class ConnectionManager extends org.melior.client.pool.ConnectionManager<RabbitMQClientConfig, Connection, org.springframework.amqp.rabbit.connection.Connection> implements org.springframework.amqp.rabbit.connection.ConnectionFactory {

    /**
     * Constructor.
     * @param configuration The client configuration
     * @param connectionFactory The connection factory
     */
    public ConnectionManager(
        final RabbitMQClientConfig configuration,
        final ConnectionFactory connectionFactory) {

        super(configuration, connectionFactory);
    }

    /**
     * Create connection.
     * @return The connection
     * @throws AmqpException if unable to create a connection
     */
    public org.springframework.amqp.rabbit.connection.Connection createConnection() throws AmqpException {

        try {

            return getConnection();
        }
        catch (Exception exception) {
            throw new AmqpException(exception.getMessage());
        }

    }

    public String getHost() {
        return null;
    }

    public int getPort() {
        return 0;
    }

    public String getVirtualHost() {
        return null;
    }

    public String getUsername() {
        return null;
    }

    public void addConnectionListener(
        final ConnectionListener listener) {
    }

    public boolean removeConnectionListener(
        final ConnectionListener listener) {
        return false;
    }

    public void clearConnectionListeners() {
    }

}
