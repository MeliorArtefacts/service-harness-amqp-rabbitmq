/* __  __    _ _      
  |  \/  |  | (_)       
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
    Service Harness
*/
package org.melior.client.rabbitmq;
import javax.net.ssl.SSLContext;

/**
 * TODO
 * @author Melior
 * @since 2.3
 */
public class RabbitMQClientBuilder{
    private boolean async = false;

    private boolean ssl = false;

    private SSLContext sslContext;

  /**
   * Constructor.
   */
  private RabbitMQClientBuilder(){
        super();
  }

  /**
   * Create RabbitMQ client builder.
   * @return The RabbitMQ client builder
   */
  public static RabbitMQClientBuilder create(){
        return new RabbitMQClientBuilder();
  }

  /**
   * Build RabbitMQ client.
   * @return The RabbitMQ client
   */
  public RabbitMQClient build(){
        return new RabbitMQClient(async, ssl, sslContext);
  }

  /**
   * Enable asynchronous transport.
   * @return The RabbitMQ client builder
   */
  public RabbitMQClientBuilder async(){
        this.async = true;

    return this;
  }

  /**
   * Enable SSL.
   * @return The RabbitMQ client builder
   */
  public RabbitMQClientBuilder ssl(){
        this.ssl = true;

    return this;
  }

  /**
   * Set SSL context.
   * @param sslContext The SSL context
   * @return The RabbitMQ client builder
   */
  public RabbitMQClientBuilder sslContext(
    final SSLContext sslContext){
        this.sslContext = sslContext;

    return this;
  }

}
