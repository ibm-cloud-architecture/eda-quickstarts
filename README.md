# EDA Quickstarts

UNDER CONSTRUCTION - Updated 07/28/2022

This project includes a set of getting started code templates for your event driven projects. 

The approach is to use one of the different starting code folder to help implementing your own event driven solution. 
Part of the IBM Garage methodology is to apply event storming and domain driven design to start your project on good track. 
Once you have discovered events, aggregates, commands and bounded contexts the next step is to start your microservice with all the needed 
artifacts to build, run locally and then deploy to OpenShift.

We try to use the same structure for code to support Domain Driven Design practices as resource, service, domain, infrastructure, api...

The main project includes the environment folder which supports different deployments:

* to run Strimzi Kafka and Apicurio locally on your laptop using docker compose
* to deploy strimzi on OpenShift with kustomize 
* to deploy event streams on OpenShift with kustomize see the [eda-gitops-catalog](https://github.com/ibm-cloud-architecture/eda-gitops-catalog)

Each project includes:

* Code template based on the same OrderEntity management
* Github action workflow to build with maven, build jvm docker image and push the image to an image registry
* Kustomize folder for yamls to deploy to OpenShift cluster which could be included in a GitOps project.

A template of GitOps project is in [the eda-quickdtart-gitops repository](https://github.com/ibm-cloud-architecture/eda-quickdtart-gitops.git) with the 2 reactive messaging services
configured and deployed.

kam bootstrap --service-repo-url https://github.com/jbcodeforce/eda-demo-order-ms --gitops-repo-url  https://github.com/jbcodeforce/eda-demo-order-gitops --image-repo quay.io/ibmcase/eda-order-rms --output eda-demo-order-gitops --prefix edademo --push-to-git=true --git-host-access-token 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
## Java templates

We are adopting Quarkus (current version is 2.5) as our main Java Microprofile framework, for the development experience, and the excellent performance to start in Kubernetes.

Here are the project

* [Reactive messaging producer](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/quarkus-reactive-kafka-producer)
* [Reactive messaging consumer](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/quarkus-reactive-kafka-consumer)

Still under construction:

* [Quarkus app with Kafka Producer API](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/quarkus-producer-kafka-api): OpenAPI, metrics, health, RESTeasy, Kafka API
* [Quarkus app with Kafka Consumer API](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/quarkus-consumer-kafka-api): OpenAPI, metrics, health, RESTeasy, Kafka API, to consume 'product' events. Deployable on OpenShift with source to image, with configMap to tune the EventStreams or Kafka client settings.


* [Quarkus app with Kafka Producer API and Avro schema](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/)
* [Quarkus Kafka producer - command part of CQRS](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/quarkus-kafka-producer) is to use Microprofile reactive messaging outbound channel which produces `create events` when data is pushed via REST POST end point. The basic Order entity is here as a placeholder to support REST operation, event creation and persistence. The repository is a Postgresql access layer using Quarkus Panache and hibernate ORM. The code generates to `orders` topic.
* [Spring Cloud Stream](https://github.com/ibm-cloud-architecture/eda-quickstarts/tree/main/spring-cloud-stream)

Other projects that could be used as template / source of inspiration:

* [Order CQRS with Open Liberty and Kafka API](https://github.com/ibm-cloud-architecture/refarch-kc-order-ms)
* [Outbox pattern with Postgresql, Debezium Outbox Quarkus plugin and Debezium change data capture with Kafka Conncet](https://github.com/ibm-cloud-architecture/vaccine-order-mgr-pg)
