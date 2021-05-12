# Template project for a Quarkus app with Kafka producer API

## What the template does

* Use Quarkus and Microprofile 3.0 for health, metrics and open API.
* Use Kafka producer API, with Avro serialization
* Use [Avro maven plugin](https://avro.apache.org/docs/current/gettingstartedjava.html#Serializing+and+deserializing+without+code+generation) to generate code from Avro definitions, taking into account the order of import to manage schema dependencies
* Use Apicurio schema registry and the maven plugin to upload new definition to the connected registry.


## Code structure

The code is reusing the Domain Driven Design approach layers to organize the code:

* **infrastructure**: to include lower level integration layer. This is where to find repository or kafka lower level component if needed
* **domain**: domain model and services supporting the business logic - Events are generate under the domain. It could have bean generated under infrastructure.
* **app**: APIs and application related classes to make it running.

The REST resource delegates to a service where you may want to implement some business logic there. The service class should be tested by isolation. The Resource is doing simple data mapping between the model for the query, creation or update of the main business entity, in this example the Order. 

The GreetingResource could be deleted, it was created during the Quarkus app creation.

The repository is a mockup one using HashMap to keep the data, it helps to start quickly to demonstrate the application. So the event producer is using Avro schema and the pattern of writing to the topic immediately once the order is received at the API level. 

## Run and build locally

### Start local Kafka

For development purpose you can run the following command to start one kafka, one zookeeper and Apicurio for schema registry on port 8090.

```shell
docker-compose up -d
```

To create the Kafka topic, you may need to update this script: `scripts/createTopics.sh` and change the topic name or add more line for other topics

```shell
./scripts/createTopics.sh
```

Going to the URL: [http://localhost:8090/ui/artifacts](http://localhost:8090/ui/artifacts) to see the schema in the registry.

### Update to schema

The Apache Avro schemas are in `src/main/avro`. The package name defined in the avro namespace attribute, will map to the Java packages. Use the following command to update the Java classes and upload the schema to the registry. See next section on how to start Kafka and Apicurio.

```shell
mvn generate-sources
```

You should see messages like this in the terminal log:

```
Successfully registered artifact [OrderGroup] / [OrderEvent].  GlobalId is [1]
```
Each time this goal is run, a new version of the schema is created. Remark: `mvn install` will also perform this maven goal.

In docker compose we are using Apicurio with Kafka persistence. So a topic is create named `kafkasql-journal` to persist any actions on the schema. 

### Running the application in dev mode

You can run your application in dev mode which enables live coding using:

```
./mvnw quarkus:dev
```

Use the Swagger UI to access existing orders (loaded from the `resources/orders.json` file) and to post new order.

[http://localhost:8080/q/swagger-ui/](http://localhost:8080/q/swagger-ui/).

### Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `order-mgr-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/order-mgr-1.0-SNAPSHOT-runner.jar`.

The script: `./scripts/buildAll.sh` do the maven packaging and then build a docker images.

### Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/order-mgr-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.