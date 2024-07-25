/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.service.rabbitmq;
import org.melior.client.core.ClientConfig;
import org.melior.util.number.Clamp;

/**
 * Configuration parameters for a {@code RabbitMQListener}, with defaults.
 * @author Melior
 * @since 2.3
 */
public class RabbitMQListenerConfig extends ClientConfig {

    private int consumers = 1;

    private int prefetch = 1;

    /**
     * Constructor.
     */
    protected RabbitMQListenerConfig() {

        super();
    }

    /**
     * Get consumers.
     * @return The consumers
     */
    public int getConsumers() {
        return consumers;
    }

    /**
     * Set consumers.
     * @param consumers The consumers
     */
    public void setConsumers(
        final int consumers) {
        this.consumers = Clamp.clampInt(consumers, 1, Integer.MAX_VALUE);
    }

    /**
     * Get prefetch count.
     * @return The prefetch count
     */
    public int getPrefetch() {
        return prefetch;
    }

    /**
     * Set prefetch count.
     * @param prefetch The prefetch count
     */
    public void setPrefetch(
        final int prefetch) {
        this.prefetch = Clamp.clampInt(prefetch, 1, Integer.MAX_VALUE);
    }

}
