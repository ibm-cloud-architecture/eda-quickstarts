# EDA Quickstarts

A set of getting started code templates for your event driven journey. The approach is to use one of the different starting code folder to start implementing your event driven solution. As part of the garage methodology is to apply event storming and domain driven design to start your project on good track. Once you have discovered events, aggregates, commands and bonded context the next step is to start your microservices with all the needed artifacts to build, run locally and then deploy to OpenShift. It has also CI/CD elements with GitOps and Kustomize.

We try to use the same structure for code to support Domain Driven Design ubiquitous language as resource, service, domain, infrastructure, api...

## Java

* [Quarkus-kafka-producer]() is to use Microprofile reactive messaging outbound channel with produce to create events from data created from a REST POST end point. The basic Order entity is here as a placeholder to support REST operation, event creation and persistence. The repository is a Postgresql access layer using Quarkus Panache and hibernate ORM. The code generates to `orders` topic.