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
import org.springframework.amqp.rabbit.connection.AbstractConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import com.rabbitmq.client.ShutdownListener;

/**
 * Implements a factory for raw RabbitMQ {@code Connection} objects.
 * @author Melior
 * @since 2.3
 */
public class RabbitMQConnectionFactory extends AbstractConnectionFactory implements ShutdownListener {

    /**
     * Constructor.
     */
    public RabbitMQConnectionFactory() {

        super(newRabbitConnectionFactory());

        doSetPublisherConnectionFactory(new RabbitMQConnectionFactory(getRabbitConnectionFactory(), true));
    }

    /**
     * Constructor.
     * @param rabbitConnectionFactory The raw connection factory
     * @param isPublisherFactory true if the factory is a publisher factory, false otherwise
     */
    private RabbitMQConnectionFactory(
        final com.rabbitmq.client.ConnectionFactory rabbitConnectionFactory,
        final boolean isPublisherFactory) {

        super(rabbitConnectionFactory);

        setPublisherConnectionFactory((isPublisherFactory == false) ? new RabbitMQConnectionFactory(getRabbitConnectionFactory(), true) : null);
    }

    /**
     * Create raw connection factory.
     * @return The raw connection factory
     */
    private static com.rabbitmq.client.ConnectionFactory newRabbitConnectionFactory() {

        com.rabbitmq.client.ConnectionFactory connectionFactory;

        connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        connectionFactory.setAutomaticRecoveryEnabled(false);

        return connectionFactory;
    }

    /**
     * Create connection.
     * @return The connection
     * @throws AmqpException if unable to create a connection
     */
    public final Connection createConnection() throws AmqpException {

        return super.createBareConnection();
    }

}
