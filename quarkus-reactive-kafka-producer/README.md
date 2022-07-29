# Quarkus app for producing event to Kafka via reactive messaging and AVRO

Updated 7/27/2022
## What the template does

* Use Quarkus and Microprofile 3.0 for health, metrics and open API extensions
* Use Microprofile reactive messaging API, with Json schema and Avro serialization
* Use Apicurio schema registry and the maven plugin to upload new definition to the connected registry.
* Support Domain driven design practices

The applications was created with the following extensions:

```sh
quarkus create app quarkus-reactive-kafka-producer --extension=resteasy-reactive-jackson,smallrye-reactive-messaging-kafka,apicurio-registry-avro,openshift
quarkus ext add smallrye-openapi smallrye-health  
```

`apicurio-registry-avro` automatically creates Java Beans from avsc files saved in `src/main/avro` to the folder `target/generated-sources/avsc` which should be in your IDE java source path so you own class  can import those beans.

* [See the schema registry avro quarkus guide](https://quarkus.io/guides/kafka-schema-registry-avro)
* [See Dev Services for Kafka](https://quarkus.io/guides/kafka-dev-services)
* [and Dev Services for Apicurio Registry](https://quarkus.io/guides/apicurio-registry-dev-services)

## Code structure

The code is reusing the Domain Driven Design approach of layers to organize the code. From top down visibility we have:

* **app**: application related classes to make it running.
* **domain**: domain model and services supporting the business logic - Events are generate under the domain. It could have bean generated under infrastructure.
* **infra**: to include the integration layer. This is where to find repository, the REST api or Kafka lower level component needed.

Normally events can be considered at the domain level, as it is a business decision to 
define what data elements to share with other. It is also fine to consider them at the 
infrastructure level. In this template the avro schemas in `src/main/avro` use the package 
namespace: `ibm.eda.demo.ordermgr.infra.events` but this could be changed to 
be `ibm.eda.demo.ordermgr.domain.events`.

The REST resource delegates to a service where you may want to implement the business logic
 with the domain entities. The service class should be tested by isolation. 
The Resource class supports JAXRS and Microprofile annotation and is doing simple data mapping between the DTOs and the business entities.

The repository is a mockup, and it is using HashMap to keep the data, it helps to start 
quickly to demonstrate the application. The event producer is using Avro schema and 
the pattern of writing to the topic immediately once the order is received at the API level.

The schema management is supported by the avro serializer from Apicur.io and the declarations done in the `application.properties` like:

```yaml
mp.messaging.outgoing.orders.value.serializer=io.apicurio.registry.serde.avro.AvroKafkaSerializer
mp.messaging.connector.smallrye-kafka.apicurio.registry.url=${ES_APICURIO_URL}
# automatically register the schema with the registry, if not present
mp.messaging.outgoing.orders.apicurio.registry.auto-register=true
```

Dev Services for Apicurio Registry configures all Kafka channels in SmallRye Reactive Messaging to use the automatically started registry instance.

## How to use this template

Clone this repository and start updating tests and business entities in the domain to support
your business logic. See next sections to run the application in development mode.

## Avro schemas

Define .avsc file (JSON doc) for each type. See declarations in `src/main/avro` folder. With Quarkus 2.x the
plugin is generating java beans to the `target/generated-sources/avsc` folder which is in IDE path.

## Running the application in dev mode with redpanda

* You can run your application in dev mode that enables live coding using:

```shell script
quarkus dev
```

Kafka broker and Apicurio Registry instance are started automatically thanks to Dev Services. 

* Get the URL of Apicur.io registry user interface by looking at the port mapping done by docker 

```sh
docker ps
# you can get something like
quay.io/apicurio/apicurio-registry-mem:2.2.3.Final   "/usr/local/s2i/run"    8443/tcp, 8778/tcp, 9779/tcp, 0.0.0.0:55296->8080/tcp  ...
# launch yor web browser
chrome https://localhost:55296/ui
```

At that stage now schema were added. It will be added when the Kafka producer will produce its first message.

* Post an order via the POST api on the Order microservice, which will produce a message to Kafka

```sh
curl -X 'POST' \
  'http://localhost:8080/api/v1/orders' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d @./e2e/data/order-1.json
```

* A refresh of the Apicur.io UI will display the schemas define: Address and OrderEvent. 

* Go to the swagger UI: [http://localhost:8080/q/swagger-ui/](http://localhost:8080/q/swagger-ui/) or use
the following calls to get the connection to Kafka started:

```sh
curl -X 'GET' 'http://localhost:8080/api/v1/orders' -H 'accept: application/json'
```

* Use [redpanda rpk cli](https://vectorized.io/docs/rpk-commands/):

```sh
# go inside the broker container
docker ps 
docker exec -ti <redpanda container id> bash
# listen to messages
rpk topic consume qs-orders
```

## Running the application in dev mode with docker compose

* You must start Kafka with docker compose using the compose file under `../environment/local/strimzi`:

```sh
docker compose up -f kafka-3.2-apicurio-2.2.yaml -d
```

you should see four containers running:

```
 ⠿ Container apicurio       Started                                                                                                                     1.0s
 ⠿ Container zookeeper      Started                                                                                                                     1.0s
 ⠿ Container kafka          Started                                                                                                                     1.9s
 ⠿ Container kafdrop        Started  
```

* Create the needed topic from `environment/local/strimzi` folder:

```
./createTopic.sh
```

You can run your application in dev mode that enables live coding using:

```shell script
quarkus dev
```

* As before validate the GET /orders and then do POST with one order.


## Deploy to OpenShift

### Pre-requisites

We assume an IBM Event Streams is already deployed in the namespace `cp4i-eventstreams` and configured to have mutual TLS for authentication. As an alternative, it is possible to install Strimzi in the namespace where this application will be deploy.

## Steps to deploy the application

1. Create the namespace, role and service account

    ```sh
    oc apply -k kustomize/eda-qs/env/base
    ```

1. Make the new created project active

    ```sh
    oc project eda-qs
    ```

1. Create topic, tls user, and copy secrets for CA certificate and TLS user client certification.

    ```sh
    oc apply -k kustomize/eda-qs/services/ibm-eventstreams/overlays 
    ```

1. Deploy the application with config map...

    ```sh
    oc apply -k kustomize/eda-qs/apps/producer/overlays
    ```

1. Verify pods is running and get URL to the swagger

    ```sh
    oc get pods | grep qs-order
    export MS_URL=http://$(oc get routes qs-order-rms -o jsonpath='{.status.ingress[0].host}')
    chrome $MS_URL
    ```

1. Send a message

    ```sh
    curl -X 'POST' $MS_URL/api/v1/orders \
    -H 'accept: application/json' \
    -H 'Content-Type: application/json' \
    -d @./e2e/data/order-1.json
    ```

1. Verify the message is sent to the `eda-qs-orders` topic and schema is in the registry.

1. Clean all

    ```sh
    oc delete -k kustomize/eda-qs/apps/producer/overlays
    oc delete -k kustomize/eda-qs/services/ibm-eventstreams/overlays 
    oc delete -k kustomize/eda-qs/env/base
    ```