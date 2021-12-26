# Quarkus app for producing event to Kafka via reactive messaging and AVRO

## What the template does

* Use Quarkus and Microprofile 3.0 for health, metrics and open API extensions
* Use Microprofile reactive messaging API, with Json schema and Avro serialization
* Use Apicurio schema registry and the maven plugin to upload new definition to the connected registry.
* Support Domain driven design practices

The applications was created with the following extensions:

```sh
quarkus ext add resteasy-mutiny resteasy-jackson smallrye-openapi smallrye-health openshift reactive-messaging-kafka apicurio-registry-avro
```
`apicurio-registry-avro` automatically creates Java Beans from avsc files saved in `src/main/avro` to the folder `target/generated-sources/avsc` which
should be in the IDE java source path so you own class  can import those beans.

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
As there is kafka library it starts RedPanda container and as there is apicurio it also starts an apicur.io docker container.

* Post an order 

```sh
curl -X 'POST' \
  'http://localhost:8080/api/v1/orders' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
 
  "customerID": "C01",
    "productID": "P01",
    "quantity": 20,
    "destinationAddress": {
      "street": "12 main street",
      "city": "san francisco",
      "country": "USA",
      "state": "CA",
      "zipcode": "92000"
    }
}'
```


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
rpk topic consume orders
```

## Running the application in dev mode with docker compose

* You must start Kafka with docker compose using the compose file under `../environment/local/strimzi`:

```sh
docker compose up -d
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



