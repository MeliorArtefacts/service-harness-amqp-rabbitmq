/* __  __      _ _            
  |  \/  |    | (_)           
  | \  / | ___| |_  ___  _ __ 
  | |\/| |/ _ \ | |/ _ \| '__|
  | |  | |  __/ | | (_) | |   
  |_|  |_|\___|_|_|\___/|_|   
        Service Harness
*/
package org.melior.client.rabbitmq;
import java.lang.reflect.Method;
import org.melior.client.core.ClientObject;
import org.melior.client.exception.RemotingException;

/**
 * A wrapper around a RabbitMQ {@code Channel} object which makes the {@code Channel} persistent.
 * @author Melior
 * @since 2.3
 */
public class Channel extends ClientObject<RabbitMQClientConfig, Connection, com.rabbitmq.client.Channel> {

    /**
     * Constructor.
     * @param configuration The client configuration
     * @param connection The connection
     * @throws RemotingException if an error occurs during the construction
     */
    public Channel(
        final RabbitMQClientConfig configuration,
        final Connection connection) throws RemotingException {

        super("Channel", configuration, connection, com.rabbitmq.client.Channel.class);
    }

    /**
     * Handle proxy invocation.
     * @param object The object on which the method was invoked
     * @param method The method to invoke
     * @param args The arguments to invoke with
     * @return The result of the invocation
     * @throws Throwable if the invocation fails
     */
    public Object invoke(
        final Object object,
        final Method method,
        final Object[] args) throws Throwable {

        String methodName;
        Object invocationResult;

        methodName = method.getName();

        if (methodName.equals("close") == true) {

            invocationResult = null;
        }
        else {

            invocationResult = invoke(method, args);
        }

        return invocationResult;
    }

}
