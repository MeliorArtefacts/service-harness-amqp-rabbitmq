/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.rabbitmq;
import org.melior.client.core.ClientConfig;

/**
 * Configuration parameters for a {@code RabbitMQClient}, with defaults.
 * @author Melior
 * @since 2.3
 */
public class RabbitMQClientConfig extends ClientConfig {

    private String exchange;

    private String routingKey;

    /**
     * Constructor.
     */
    protected RabbitMQClientConfig() {

        super();
    }

    /**
     * Configure client.
     * @param clientConfig The new client configuration parameters
     * @return The client configuration parameters
     */
    public RabbitMQClientConfig configure(
        final RabbitMQClientConfig clientConfig) {
        super.configure(clientConfig);
        this.exchange = clientConfig.exchange;
        this.routingKey = clientConfig.routingKey;

        return this;
    }

    /**
     * Get exchange.
     * @return The exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * Set exchange.
     * @param exchange The exchange
     */
    public void setExchange(
        final String exchange) {
        this.exchange = exchange;
    }

    /**
     * Get routing key.
     * @return The routing key
     */
    public String getRoutingKey() {
        return routingKey;
    }

    /**
     * Set routing key.
     * @param routingKey The routing key
     */
    public void setRoutingKey(
        final String routingKey) {
        this.routingKey = routingKey;
    }

}
