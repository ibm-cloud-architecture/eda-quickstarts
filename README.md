# EDA Quickstarts

UNDER CONSTRUCTION

This project includes a set of getting started code templates for your event driven journey. 

The approach is to use one of the different starting code folder to start implementing your own event driven solution. Part of the IBM Garage methodology is to apply event storming and domain driven design to start your project on good track. Once you have discovered events, aggregates, commands and bounded context the next step is to start your microservices with all the needed artifacts to build, run locally and then deploy to OpenShift.

We try to use the same structure for code to support Domain Driven Design ubiquitous language as resource, service, domain, infrastructure, api...

## Java

* [Quarkus app with Kafka Consumer API](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/kafka-consumer-api): OpenAPI, RESTeasy, Kafka API, to consumer 'product' events. Deployable on OpenShift with source to image, with configMap to tune the EventStreams or Kafka client settings.
* [Quarkus-kafka-producer](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/quarkus-kafka-producer) is to use Microprofile reactive messaging outbound channel which produces `create events` when data is pushed via REST POST end point. The basic Order entity is here as a placeholder to support REST operation, event creation and persistence. The repository is a Postgresql access layer using Quarkus Panache and hibernate ORM. The code generates to `orders` topic.