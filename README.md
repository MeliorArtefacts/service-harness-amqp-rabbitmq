# Melior Service Harness :: AMQP : RabbitMQ
<div style="display: inline-block;">
<img src="https://img.shields.io/badge/version-2.3-green?style=for-the-badge"/>
<img src="https://img.shields.io/badge/production-ready-green?style=for-the-badge"/>
<img src="https://img.shields.io/badge/compatibility-spring_boot_2.4.5-green?style=for-the-badge"/>
</div>

## Artefact
Get the artefact and the POM file in the *artefact* folder.
```
<dependency>
    <groupId>org.melior</groupId>
    <artifactId>melior-harness-amqp-rabbitmq</artifactId>
    <version>2.3</version>
</dependency>
```

## Client
Create a bean to instantiate the RabbitMQ client.  The RabbitMQ client uses connection pooling to improve performance.
```
@Bean("myclient")
@ConfigurationProperties("myclient")
public RabbitMQClient client() {
    return RabbitMQClientBuilder.create().build();
}
```

The RabbitMQ client is auto-configured from the application properties.
```
myclient.url=amqp://some.service:5672/environment
myclient.username=user
myclient.password=password
myclient.exchange=myExchange
myclient.queue=myQueue
myclient.request-timeout=30
myclient.inactivity-timeout=300
```

Map the attributes in the JSON message to the fields in the JAVA class.
```
public class Message {
    @JsonProperty("category")
    private String category;

    @JsonProperty("type")
    private String type;

    @JsonProperty("description")
    private String description;

    @JsonProperty("volume")
    private double volume;

    @JsonProperty("balance")
    private double balance;

    ...
}
```

Wire in and use the RabbitMQ client.  There is no need to convert the JAVA object to JSON before using the RabbitMQ client to send the message.  The RabbitMQ client performs the object mapping automatically.
```
@Autowired
private RabbitMQClient client;

public void foo(Message message) throws RemotingException {
    client.send(message)
}
```

The RabbitMQ client may be configured using these application properties.

|Name|Default|Description|
|:---|:---|:---|
|`url`||The URL of the RabbitMQ server|
|`username`||The user name required by the RabbitMQ server|
|`password`||The password required by the RabbitMQ server|
|`minimum-connections`|0|The minimum number of connections to open to the RabbitMQ server|
|`maximum-connections`|1000|The maximum number of connections to open to the RabbitMQ server|
|`connection-timeout`|30 s|The amount of time to allow for a new connection to open to the RabbitMQ server|
|`validate-on-borrow`|false|Indicates if a connection must be validated when it is borrowed from the JDBC connection pool|
|`validation-timeout`|5 s|The amount of time to allow for a connection to be validated|
|`request-timeout`|60 s|The amount of time to allow for a request to the RabbitMQ server to complete|
|`backoff-period`|1 s|The amount of time to back off when the circuit breaker trips|
|`backoff-multiplier`|1|The factor with which to increase the backoff period when the circuit breaker trips repeatedly|
|`backoff-limit`||The maximum amount of time to back off when the circuit breaker trips repeatedly|
|`inactivity-timeout`|300 s|The amount of time to allow before surplus connections to the RabbitMQ server are pruned|
|`maximum-lifetime`|unlimited|The maximum lifetime of a connection to the RabbitMQ server|
|`prune-interval`|60 s|The interval at which surplus connections to the RabbitMQ server are pruned|

&nbsp;  
## References
Refer to the [**Melior Service Harness :: Core**](https://github.com/MeliorArtefacts/service-harness-core) module for detail on the Melior logging system and available utilities.
