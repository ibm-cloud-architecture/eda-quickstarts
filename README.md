# EDA Quickstarts

UNDER CONSTRUCTION - Update 6/6/2021

This project includes a set of getting started code templates for your event driven projects. 

The approach is to use one of the different starting code folder to start implementing your own event driven solution. 
Part of the IBM Garage methodology is to apply event storming and domain driven design to start your project on good track. 
Once you have discovered events, aggregates, commands and bounded contexts the next step is to start your microservice with all the needed 
artifacts to build, run locally and then deploy to OpenShift.

We try to use the same structure for code to support Domain Driven Design practices as resource, service, domain, infrastructure, api...

Each project includes:

* Github workflow to perform as action after a push to the repository
* Docker-compose file to run Kafka, Apicurio locally
* Kustomize folder for yaml and kustomization for the different deployment environment

## Java

We are adopting Quarkus as our main framework, for the development experience, and the excellent performance to start in Kubernetes.

* [Quarkus app with Kafka Consumer API](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/kafka-consumer-api): OpenAPI, metrics, health, RESTeasy, Kafka API, to consume 'product' events. Deployable on OpenShift with source to image, with configMap to tune the EventStreams or Kafka client settings.
* [Quarkus Kafka producer - command part of CQRS](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/quarkus-kafka-producer) is to use Microprofile reactive messaging outbound channel which produces `create events` when data is pushed via REST POST end point. The basic Order entity is here as a placeholder to support REST operation, event creation and persistence. The repository is a Postgresql access layer using Quarkus Panache and hibernate ORM. The code generates to `orders` topic.
* [Spring Cloud Stream](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/spring-cloud-stream)
* [Reactive messaging consumer](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/kafka-consumer-reactive-msg)

Other projects that could be used as template / source of inspiration:

* [Order CQRS with Open Liberty and Kafka API](https://github.com/ibm-cloud-architecture/refarch-kc-order-ms)
* [Outbox pattern with Postgresql, Debezium Outbox Quarkus plugin and Debezium change data capture with Kafka Conncet](https://github.com/ibm-cloud-architecture/vaccine-order-mgr-pg)
