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
import org.springframework.amqp.core.Message;

/**
 * Modifies a RabbitMQ message after it has been constructed, but before
 * it is dispatched to the RabbitMQ server.
 * <p>
 * This implementation sets the correlation id in the message properties
 * to the transaction identifier from the transaction context, to allow
 * a transaction to be traced to the RabbitMQ server.
 * @author Melior
 * @since 2.3
 */
public class MessagePostProcessor implements org.springframework.amqp.core.MessagePostProcessor {

    private String correlationId;

    /**
     * Constructor.
     * @param correlationId The correlation identifier
     */
    public MessagePostProcessor(
        final String correlationId) {

        super();

        this.correlationId = correlationId;
    }

    /**
     * Post-process message.
     * @param message The message
     * @return The message
     * @throws AmqpException if unable to process the message
     */
    public Message postProcessMessage(
        final Message message) throws AmqpException {

        message.getMessageProperties().setCorrelationId(correlationId);

        return message;
    }

}
