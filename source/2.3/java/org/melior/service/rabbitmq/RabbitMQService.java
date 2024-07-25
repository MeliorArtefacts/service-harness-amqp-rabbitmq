/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.service.rabbitmq;
import org.melior.context.service.ServiceContext;
import org.melior.service.core.AbstractService;
import org.melior.service.exception.ApplicationException;

/**
 * A base class for service implementations that process messages from RabbitMQ queues.
 * <p>
 * The service implementation class is furnished with important constructs like the
 * service context, the service configuration and a logger.
 * <p>
 * If the service implementation class registers a RabbitMQ queue with a {@code RabbitMQListener}
 * then this class installs a {@code RabbitMQRequestInterceptor} to intercept the messages
 * that have arrived in the queue after they have been retrieved by the {@code RabbitMQListener}
 * but before they have been processed, and routes them past the configured {@code WorkManager}
 * to allow the {@code WorkManager} to control the flow of the messages through the application.
 * @author Melior
 * @since 2.3
 * @see RabbitMQRequestInterceptor
 * @see AbstractService
*/
public abstract class RabbitMQService extends AbstractService {

    /**
     * Bootstrap service.
     * @param serviceClass The service class
     * @param args The command line arguments
     */
    public static void run(
        final Class<?> serviceClass,
        final String[] args) {

        AbstractService.run(serviceClass, args, true);
    }

    /**
     * Constructor.
     * @param serviceContext The service context
     * @throws ApplicationException if an error occurs during the construction
     */
    public RabbitMQService(
        final ServiceContext serviceContext) throws ApplicationException {

        super(serviceContext, true);
    }

    /**
     * Register queue to listen to.  The queue is the default one as configured on the RabbitMQ client.
     * @param <T> The type
     * @param rabbitMQListener The RabbitMQ listener
     * @return The RabbitMQ queue
     * @throws ApplicationException if unable to register the queue
     */
    protected <T> RabbitMQQueue<T> registerDefaultQueue(
        final RabbitMQListener<T> rabbitMQListener) throws ApplicationException {

        RabbitMQQueue<T> queue;

        queue = rabbitMQListener.registerDefaultInterceptor();

        return queue;
    }

    /**
     * Register queue to listen to.
     * @param <T> The type
     * @param rabbitMQListener The RabbitMQ listener
     * @param queueName The queue name
     * @return The RabbitMQ queue
     * @throws ApplicationException if unable to register the queue
     */
    protected <T> RabbitMQQueue<T> register(
        final RabbitMQListener<T> rabbitMQListener,
        final String queueName) throws ApplicationException {

        RabbitMQQueue<T> queue;

        queue = rabbitMQListener.registerInterceptor(queueName);

        return queue;
    }

}
