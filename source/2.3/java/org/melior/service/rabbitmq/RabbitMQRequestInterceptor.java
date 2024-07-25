/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.service.rabbitmq;
import java.util.UUID;
import org.melior.context.service.ServiceContext;
import org.melior.context.transaction.TransactionContext;
import org.melior.logging.core.Logger;
import org.melior.logging.core.LoggerFactory;
import org.melior.service.exception.ApplicationException;
import org.melior.service.exception.ExceptionType;
import org.melior.service.work.SingletonProcessor;
import org.melior.service.work.WorkManager;

/**
 * Intercepts any messages that have been retrieved by the {@code RabbitMQListener}, before
 * they have been processed, and routes them past the configured {@code WorkManager}
 * to allow the {@code WorkManager} to control the flow of the messages through
 * the application.
 * <p>
 * The transaction context is populated with an automatically generated UUID
 * for the duration of processing of the messages.
 * @author Melior
 * @since 2.3
 */
public class RabbitMQRequestInterceptor<T> extends RabbitMQQueue<T> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SingletonProcessor<T> processor;

    private WorkManager workManager;

    /**
     * Constructor.
     * @param listener The listener
     * @param name The name of the queue
     * @param capacity The capacity of the queue
     */
    RabbitMQRequestInterceptor(
        final RabbitMQListener<T> listener,
        final String name,
        final int capacity) {

        super(listener, name, capacity);

        this.workManager = ServiceContext.getWorkManager();
    }

    /**
     * Set processor.  New arrivals in the queue
     * will be processed individually.
     * @param processor The processor
     * @return The RabbitMQ queue
     */
    public RabbitMQQueue<T> process(
        final SingletonProcessor<T> processor) {
        this.processor = processor;
        super.process(message -> process(message));

        return this;
    }

    /**
     * Process message.
     * @param message The message
     * @throws ApplicationException if unable to process the message
     */
    protected void process(
        final T message) throws ApplicationException {

        boolean isException = false;
        String operation;

        operation = getOperation();

        startRequest(operation);

        try {

            processor.process(message);
        }
        catch (ApplicationException exception) {

            isException = true;

            throw exception;
        }
        catch (Throwable exception) {

            isException = true;

            throw new ApplicationException(ExceptionType.UNEXPECTED, "Failed to process message: " + exception.getMessage());
        }
        finally {

            completeRequest(isException);
        }

    }

    /**
     * Start processing request.
     * @param operation The operation
     * @throws ApplicationException if unable to start processing the request
     */
    public final void startRequest(
        final String operation) throws ApplicationException {

        String methodName = "startRequest";
        TransactionContext transactionContext;

        transactionContext = TransactionContext.get();

        transactionContext.startTransaction();
        transactionContext.setTransactionId(getTransactionId());
        transactionContext.setOperation(operation);

        try {

            workManager.startRequest(transactionContext);
        }
        catch (ApplicationException exception) {
            logger.error(methodName, "Failed to notify work manager that request has started: ", exception.getMessage(), exception);

            throw exception;
        }
        catch (Exception exception) {
            logger.error(methodName, "Failed to notify work manager that request has started: ", exception.getMessage(), exception);

            throw new ApplicationException(ExceptionType.UNEXPECTED, exception.getMessage());
        }

    }

    /**
     * Complete processing request. 
     * @param isException true if the response is an exception, false otherwise
     */
    public final void completeRequest(
        final boolean isException) {

        String methodName = "completeRequest";
        TransactionContext transactionContext;

        transactionContext = TransactionContext.get();

        try {

            workManager.completeRequest(transactionContext, isException);
        }
        catch (Exception exception) {
            logger.error(methodName, "Failed to notify work manager that request has completed: ", exception.getMessage(), exception);
        }

        transactionContext.reset();
    }

    /**
     * Get operation.
     * @return The operation
     */
    private String getOperation() {
        return "rabbitmq/" + getName();
    }

    /**
     * Get transaction identifier.  Generates a UUID.
     * @return The resultant transaction identifier
     */
    private String getTransactionId() {

        return UUID.randomUUID().toString();
    }

}
