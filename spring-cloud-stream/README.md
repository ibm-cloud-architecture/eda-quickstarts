# Starter code for Kafka app using Spring Cloud Stream

This application provides the boiler plate for a event-driven microservice that support CRUD operation on a business entity (Order) and uses Kafka to share information about the order.

## Build and run locally

Use environment variables declare in a `.env` file to specify the following:

```
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

## Deploy to OpenShift


## Code explanation

The Application class declares a Spring boot application, and a bean which is supported by a java function and will act as a consumer of OrderEvents message coming from the input binding. The code uses the Spring Cloud Stream APIs. 

The Controller is using a service to support the business logic. The service process the message and sends it a topic. 