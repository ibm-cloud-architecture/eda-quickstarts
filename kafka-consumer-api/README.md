# Product manager service project

This project is a template for a Kafka Consumer service to consume from `products` topic using the Kafka Consumer API.
It uses Quarkus and resteasy to expose some basic API to be used for demonstration point of view. It uses Swagger UI and OpenAPI to document the API and expose a simple UI to trigger some of the verbs.

Kafka core and client is used. The consumer can consume n messages, so it can be used to demonstrate Mirror Maker 2, failover scenario. See the [Active - Passive lab](https://ibm-cloud-architecture.github.io/refarch-eda/use-cases/kafka-mm2/lab-3/) for more information.

The application is pre-configured to consume n messages. See property:

```
kafka.consumer.poll.records=20
```

If you want to loop forever change this parameter to -1.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw compile quarkus:dev
```

As the code is using Kafka you can use the docker compose from [this folder](../environment/strimzi) and start it as:

```shell
docker-compose -f ../environment/strimzi/docker-compose up -d
```

## Demo locally

Once Kafka and Zookeeper are started, to send 10 messages, use the Producer code from the [refarch-eda-tool project](https://github.com/ibm-cloud-architecture/refarch-eda-tools/blob/master/labs/mirror-maker2/producer/SendProductToKafka.py) with our python docker image like

```shell
docker run -ti -v $(pwd):/home --rm -e KAFKA_BROKERS=localhost:9092 ibmcase/kcontainer-python:itgtests python /home/producer/SendProductToKafka.py --random 10
```

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `code-with-quarkus-1.0.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/demo-product-mgr-1.0.0-runner.jar`.

## Creating a native executable

You can create a native executable using: 

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/demo-product-mgr-1.0.0-runner.jar`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.html.

## Deploy to OpenShift

Be sure to get the Kafka Certificate secret copied to the project where you want to deploy this app, using a command like:

```shell
oc project yoursandbox
oc get secret light-es-cluster-ca-cert -n eventstreams --export -o yaml | oc apply -f - 
```

The update the configMap from with the broker properties and then deploy this CM:

```shell
oc apply -f src/main/kubernetes/configmap.yaml 
```

The OpenShift extension and Kubernetes config extensions were added, so once logged to the OpenShift Cluster to the target project do the following to build the jar locally and use source to image to build the docker image, publish in the private registry.

```shell
mvn clean package -Dquarkus.kubernetes.deploy=true -DskipTests
```

See the [application.properties](src/main/resources/application.properties)

```
quarkus.container-image.registry=image-registry.openshift-image-registry.svc:5000
quarkus.container-image.name=product-mgr
quarkus.container-image.group=ibmcase
quarkus.container-image.version=0.0.1

```

## More reading

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

[RESTEasy JAX-RS Guide]: https://quarkus.io/guides/rest-json

More On IBM Event Driven Architecture: https://ibm-cloud-architecture.github.io/refarch-eda/