/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.service.rabbitmq;
import org.melior.client.exception.RemotingException;
import org.melior.service.work.SingletonProcessor;
import org.melior.util.number.Counter;

/**
 * A RabbitMQ queue which is registered with a {@code RabbitMQListener} for
 * processing.  If a {@code processor} is provided when the queue
 * is built, then any new messages that are published to the queue will be
 * processed by the {@code RabbitMQListener} individually.
 * @author Melior
 * @since 2.3
 * @see SingletonProcessor
 */
public class RabbitMQQueue<T> {

    private RabbitMQListener<T> listener;

    private String name;

    private SingletonProcessor<T> processor;

    private Counter totalMessages;

    private Counter failedMessages;

    /**
     * Constructor.
     * @param listener The listener
     * @param name The name of the queue
     * @param capacity The capacity of the queue
     */
    RabbitMQQueue(
        final RabbitMQListener<T> listener,
        final String name,
        final int capacity) {

        super();

        this.listener = listener;

        this.name = name;

        totalMessages = Counter.of(0);
        failedMessages = Counter.of(0);
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

        return this;
    }

    /**
     * Start listening to queue.
     * @throws RemotingException if unable to start listening to the queue
     */
    public void start() throws RemotingException {
        listener.start(this);
    }

    /**
     * Get name.
     * @return The name
     */
    String getName() {
        return name;
    }

    /**
     * Get processor.
     * @return The processor
     */
    SingletonProcessor<T> getProcessor() {
        return processor;
    }

    /**
     * Get total number of messages.
     * @return The total number of messages
     */
    public Counter getTotalMessages() {
        return totalMessages;
    }

    /**
     * Get number of failed messages.
     * @return The number of failed messages
     */
    public Counter getFailedMessages() {
        return failedMessages;
    }

}
