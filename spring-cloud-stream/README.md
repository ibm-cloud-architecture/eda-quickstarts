# Starter code for Kafka app using Spring Cloud Stream

This application provides the boiler plate for a event-driven microservice that support CRUD operation on a business entity (Order) and uses Kafka to share information about the order.


## Code explanation

The Application class declares a Spring boot application, and a bean which is supported by a java function and acts as a consumer of OrderEvent messages coming from the input binding. The code uses the Spring Cloud Stream APIs. 

The Controller is using a service to support the business logic. The service processes the message and sends it a topic via Kafka binders. The following figure illustrates the flow: the API exposes a POST operation for new order to come, the service is the producer bean and sends the message directly to Kafka. 

![](./docs/spring-orderms-app.png)

Then the message is consumed and processed by the repository bean that persists the data to its external datasource (the code template does not use any Database, but a hashmap).

The way to generate events from a POST or any internal processing is to use  Spring [StreamBridge construct](https://github.com/spring-cloud/spring-cloud-stream/blob/master/spring-cloud-stream/src/main/java/org/springframework/cloud/stream/function/StreamBridge.java), which exposes a send function to send the record.

```java
    @Autowired
	private StreamBridge streamBridge;

    public Order processANewOrder(Order order) {
        order.status = OrderStatus.OPEN;
        order.orderID = UUID.randomUUID().toString();
        order.creationDate = LocalDate.now();
        Message<Order> toSend = MessageBuilder.withPayload(order)
            .setHeader(KafkaHeaders.MESSAGE_KEY, order.customerID.getBytes())
            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();
        streamBridge.send(BINDING_NAME, toSend);
        return order;
    }
```

The send is not sending directly the order, but use a [Message object](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/messaging/Message.html) to help defining header so we can control the behavior of Spring Cloud Stream. The `KafkaHeaders.MESSAGE_KEY` helps defining the Kafka Record Key to be set in the Kafka Record. 

### Defining Cloud Events and Event schema

The best practice to define Kafka topic content is to use Avro Schema. The events can be also encapsulated in the Cloud Events structure. The [Java SDK for CloudEvents](https://github.com/cloudevents/sdk-java) is a collection of Java packages to adopt CloudEvents in your Java application. As of now it does not support Avro serialization, so we are defining our own `CloudEvent.avsc` envelop and use another schema for the payload.

```json
{   
    "namespace": "ibm.eda.demo.infrastructure.events",
    "doc": "Avro data schema for Order domain Cloud Events",
    "type":"record",
    "name":"CloudEvent",
    "fields":[
            {
                "name": "type",
                "type": "string",
                "doc": "Type of event"
            },
            ...
            {
                "name": "data",
                "type": ["ibm.eda.demo.infrastructure.events.OrderEvent"],
                "doc": "The event"
            }
```

The pom.xml has declarations to get the event generated as Java classes under the package: `ibm.eda.demo.infrastructure.events`. The order of the imports is important as Cloud Event will include all schemas defining the Payload. 

```
<plugin>
				<groupId>org.apache.avro</groupId>
				<artifactId>avro-maven-plugin</artifactId>
				<version>${avro.version}</version>
				<executions>
					<execution>
						<phase>generate-sources</phase>
						<goals>
							<goal>schema</goal>
						</goals>
						<configuration>
							<sourceDirectory>src/main/avro/</sourceDirectory>
							<outputDirectory>src/main/java/</outputDirectory>
							<!-- need to specify order for dependencies -->
							<imports>
								<import>src/main/avro/OrderEvent.avsc</import>
								<import>src/main/avro/CloudEvent.avsc</import>
							</imports>
							<stringType>String</stringType>
						</configuration>
					</execution>
				</executions>
			</plugin>
```

As of now only one message type is defined in the payload, but you can add more and in this case add the declaration in the list of schemas: e.g. "ibm.eda.demo.infrastructure.events.ShipmentEvent".

```
 {
                "name": "data",
                "type": ["ibm.eda.demo.infrastructure.events.OrderEvent","ibm.eda.demo.infrastructure.events.ShipmentEvent"],
                "doc": "The event"
            }
```

## Build and run locally

The following figure illustrates the current docker compose environment with Kafka, Zookeeper and Apicurio:

![](./docs/docker-kafka.png)

For external to docker network connection we will use localhost:9092, and any other internal to docker network app will use `kafka:29092` connection. The docker compose for the kafka container configure the internal and external listerner as:

```
KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka:29092,EXTERNAL://localhost:9092
KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT
KAFKA_LISTENERS: EXTERNAL://0.0.0.0:9092,INTERNAL://kafka:29092
KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
```

* Use environment variables declared in a `.env` file to specify the following:

```properties
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

* Then build with `./mvnw clean package`
* Start Kafka, Zookeeper, Apicurio and Kafdrop locally with: `docker-compose up -d`
* Create the needed topics: `./scripts/createTopics.sh`
* Run the app with:

```shell
java -jar target/spring-cloud-stream-0.0.1.jar
```

Do integration smoke test with: `./e2e/postOrders.sh` which should post five new orders to the `/orders` API, which should generate events to the `orders` topic. As the record includes a key based on the customerID, each records with the same key will go to the same partition:

![](./docs/orders-topic.png)

Kafdrop is started with docker compose, and accessible to the URL: [http://localhost:9000/](http://localhost:9000/)


## Deploy to OpenShift

