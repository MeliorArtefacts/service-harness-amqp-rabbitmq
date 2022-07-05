/* __  __    _ _      
  |  \/  |  | (_)       
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
    Service Harness
*/
package org.melior.client.rabbitmq;
import org.melior.client.core.ClientConfig;

/**
 * TODO
 * @author Melior
 * @since 2.3
 */
public class RabbitMQClientConfig extends ClientConfig{
    private String exchange;

    private String queue;

  /**
   * Constructor.
   */
  protected RabbitMQClientConfig(){
        super();
  }

  /**
   * Configure client.
   * @param clientConfig The new client configuration parameters
   * @return The client configuration parameters
   */
  public RabbitMQClientConfig configure(
    final RabbitMQClientConfig clientConfig){
    super.configure(clientConfig);
    this.exchange = clientConfig.exchange;
    this.queue = clientConfig.queue;

    return this;
  }

  /**
   * Get exchange.
   * @return The exchange
   */
  public String getExchange(){
    return exchange;
  }

  /**
   * Set exchange.
   * @param exchange The exchange
   */
  public void setExchange(
    final String exchange){
    this.exchange = exchange;
  }

  /**
   * Get queue.
   * @return The queue
   */
  public String getQueue(){
    return queue;
  }

  /**
   * Set queue.
   * @param queue The queue
   */
  public void setQueue(
    final String queue){
    this.queue = queue;
  }

}
