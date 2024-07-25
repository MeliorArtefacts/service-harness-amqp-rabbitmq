/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.service.rabbitmq;
import org.melior.client.rabbitmq.RabbitMQClient;

/**
 * Convenience class for building a {@code RabbitMQListener}.  Requires a
 * {@code RabbitMQClient} to be provided.
 * @author Melior
 * @since 2.3
 * @see RabbitMQClient
 */
public class RabbitMQListenerBuilder<T> {

    private Class<T> entityClass;

    private RabbitMQClient rabbitMQClient;

    /**
     * Constructor.
     * @param entityClass The entity class
     */
    private RabbitMQListenerBuilder(
        final Class<T> entityClass) {

        super();

        this.entityClass = entityClass;
    }

    /**
     * Create RabbitMQ listener builder.
     * @param <T> The type
     * @param entityClass The entity class
     * @return The RabbitMQ listener builder
     */
    public static <T> RabbitMQListenerBuilder<T> create(
        final Class<T> entityClass) {

        return new RabbitMQListenerBuilder<T>(entityClass);
    }

    /**
     * Build RabbitMQ listener.
     * @return The RabbitMQ listener
     * @throws RuntimeException if unable to build the RabbitMQ listener
     */
    public RabbitMQListener<T> build() {

        if (rabbitMQClient == null) {
            throw new RuntimeException( "RabbitMQ client must be provided.");
        }

        return new RabbitMQListener<T>(entityClass, rabbitMQClient);
    }

    /**
     * Set RabbitMQ client.
     * @param rabbitMQClient The RabbitMQ client
     * @return The RabbitMQ listener builder
     */
    public RabbitMQListenerBuilder<T> client(
        final RabbitMQClient rabbitMQClient) {

        this.rabbitMQClient = rabbitMQClient;

        return this;
    }

}
