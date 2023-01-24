/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.rabbitmq;
import org.melior.client.exception.RemotingException;
import org.melior.client.pool.ConnectionPool;

/**
 * Implements a factory for persistent RabbitMQ {@code Connection} objects.
 * @author Melior
 * @since 2.3
 */
public class ConnectionFactory implements org.melior.client.core.ConnectionFactory<RabbitMQClientConfig, Connection, org.springframework.amqp.rabbit.connection.Connection> {

    private RabbitMQConnectionFactory connectionFactory;

    /**
     * Constructor.
     * @param async The asynchronous transport indicator
     * @param configuration The client configuration
     */
    public ConnectionFactory(
        final boolean async,
        final RabbitMQClientConfig configuration) {

        super();

        connectionFactory = new RabbitMQConnectionFactory();
        connectionFactory.setUri(configuration.getUrl());
        connectionFactory.setUsername(configuration.getUsername());
        connectionFactory.setPassword(configuration.getPassword());
        connectionFactory.setCloseTimeout(configuration.getConnectionTimeout());
        connectionFactory.setConnectionTimeout(configuration.getConnectionTimeout());
    }

    /**
     * Create a new connection.
     * @param configuration The client configuration
     * @param connectionPool The connection pool
     * @return The new connection
     * @throws RemotingException if unable to create a new connection
     */
    public Connection createConnection(
        final RabbitMQClientConfig configuration,
        final ConnectionPool<RabbitMQClientConfig, Connection, org.springframework.amqp.rabbit.connection.Connection> connectionPool) throws RemotingException {

        Connection connection;

        connection = new Connection(configuration, connectionPool, connectionFactory);
        connection.open();

        return connection;
    }

    /**
     * Destroy the connection.
     * @param connection The connection
     */
    public void destroyConnection(
        final Connection connection) {

        connection.close();
    }

}
