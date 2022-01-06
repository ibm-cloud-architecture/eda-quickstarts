# Template project for a Quarkus app with Kafka producer API

## What the template does

* Use Quarkus 2.5.1 and Microprofile 3.0 for health, metrics and open API extensions
* Use Kafka producer API, with Json schema and Avro serialization
* Use Apicurio schema registry Version 1.3.2 to be compatible with Event Streams Schema Registry. 
As quarkus deev 2.5.x is using Apicurio 2.1.x, we use docker compose and the apicurio maven plugin to upload new definition(s) to the connected registry.
* Support Domain driven design practices
* Use a Strimzi-Kafka test container class 

## Code structure

The code is reusing the Domain Driven Design approach of layers to organize the code. From top down visibility we have:

* **app**: application related classes to make it running.
* **domain**: domain model and services supporting the business logic - Events are generate under the domain. It could have bean generated under infrastructure.
* **infra**: to include the integration layer. This is where to find repository, the REST api or Kafka lower level component needed.

Normally events can be considered at the domain level, as it is a business decision to define what data elements to share with other. 
It is also fine to consider them at the infrastructure level. In this template the avro schemas in `src/main/avro` use the package namespace: `ibm.eda.demo.ordermgr.infra.events` but this could
be changed to be `ibm.eda.demo.ordermgr.domain.events`

The REST resource delegates to a service where you may want to implement the business logic with the domain entities. 
Will the service class it is easier to unit test the business logic by isolation. 
The Resource class supports JAXRS and Microprofile annotation and is doing simple data mapping between the DTOs and the business entities. 

The repository is a mockup, and it is using HashMap to keep the data, it helps to start quickly to demonstrate the application. 
The event producer is using Avro schema and the pattern of writing to the topic immediately once the order is received at the API level. 

The code proposes two KafkaProducers, one with schema registry integration, one without. 

Each producer is annotated with a CDI name, 

```java
@Singleton
@Named("avroProducer")
public class OrderEventProducerWithSchema implements EventEmitter {
..
```

and the injection is controlled in the service using the producer as:

```java
@Inject
@Named("avroProducer")
public EventEmitter eventProducer;
```

## How to use this template

Clone this repository and start updating tests and business entities in the domain 
to support your own business logic.

## Run and build locally

### Start local Kafka

For development purpose you can run the following command to start one kafka, one zookeeper and Apicurio for schema registry on port 8090.

```shell
# under eda-quickstart/environment/local/strimzi 
docker-compose -f kafka-2.8-apicurio-1.3.yaml up -d
```

you should see four containers running:

```
 ⠿ Container apicurio       Started                                                                                                                     1.0s
 ⠿ Container zookeeper      Started                                                                                                                     1.0s
 ⠿ Container kafka          Started                                                                                                                     1.9s
 ⠿ Container kafdrop        Started  
```

To create the Kafka `orders` topic:

```shell
# under eda-quickstart/environment/local/strimzi 
./createTopics.sh
```

* Start dev mode:

```sh
quarkus dev
```

Package the application and upload schema defined in `src/main/avro` to Apicurio registry with:

```sh
./mvnw package
```

Going to the URL: [http://localhost:8090/ui/artifacts](http://localhost:8090/ui/artifacts) to see the schema in the registry.

### Update to schema

The Apache Avro schemas are in `src/main/avro`. The package name defined in the avro namespace attribute, will map to the Java packages. Use the following command to update the Java classes and upload the schema to the registry. See next section on how to start Kafka and Apicurio.

```shell
mvn generate-sources
```

You should see messages like this in the terminal log:

```sh
Successfully registered artifact [OrderGroup] / [OrderEvent].  GlobalId is [1]
```

Each time this goal is run, a new version of the schema is created. Remark: `mvn install` will also perform this maven goal.

In docker compose we are using Apicurio with Kafka persistence. So the `kafkasql-journal`  topic is created, to persist any actions on the schema. 

### Running the application in dev mode

You can run your application in dev mode which enables live coding using:

```sh
quarkus dev
# or the older way
./mvnw quarkus:dev
```

Use the [Swagger UI](http://localhost:8080/q/swagger-ui/) to access existing orders (loaded from the `resources/orders.json` file) and to post new order.
Or use the following calls to get the connection to Kafka started:

```sh
curl -X 'GET' 'http://localhost:8080/api/v1/orders' -H 'accept: application/json'
```

Use [apicurio user interface](http://localhost:8090/ui/) to verify the order group order event schema was uploaded.

If you create an order via a POST, verify the order events is in the topic using the command:

```sh
./scripts/verifyOrderTopicContent.sh
```

* Go to Kafdrop console [http://localhost:9000/](http://localhost:9000/) to see messages in topic.


> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.


### Packaging the application and push to Image repository

The script: `./scripts/buildAll.sh` do the maven packaging and then build a docker images.

### Creating a native executable

You can create a native executable using: 

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/order-ms-1.0.0-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Deploy to Openshift

Two mode of deployment:

### Deploy with quarkus plugin to build the image in OpenShift

```sh
./mvnw clean package -Dquarkus.container-image.build=true
```

### Deploy using Kustomize

```sh
oc apply -k kustomize
```

## Integrating with GitOps


In the `kustomize` folder we have defined configmap, deployment,... that you can reuse to
deploy your app to OpenShift. 
Update the `deployment.yaml` to reflect the secret names you are using for TLS user and ca cert.

Doing an `oc apply -k kustomize` will deploy the current
`quay.io/ibmcase/eda-qs-order-ms` image to an OpenShift project. 

The following elements are created:

```
serviceaccount/qs-prod-sa created
rolebinding.rbac.authorization.k8s.io/app-sa-view created
configmap/qs-order-mgr-cm created
service/eda-qs-order-ms created
deployment.apps/qs-order-ms created
kafkatopic.eventstreams.ibm.com/qs-orders created
route.route.openshift.io/qs-order-ms created
```


If you use OpenShift GitOps to deploy your solution, you can create your GitOps project with the kam CLI 
and then create a folder in the `environment/dev/apps` with the name
of your app based on this code, then copy the `kustomize` folder content under this newly
created folder. After that you need to add an Argocd app under the `config` folder.



### Build and deploy on OpenShift using source to image

We assume you have Event Streams  or Strimzi cluster deployed.

* Create a OpenShift project: `oc new-project estest`
* Copy secrets for ca-certificates and tls-user

  ```sh
    # use namespace where kafka runs
    NSSRC=eventstreams NSTGT=estest SECRET=tlsuser \
  	oc get secret $SECRET --namespace=$NSSRC -o json \
	| jq  'del(.metadata.uid, .metadata.selfLink, .metadata.creationTimestamp, .metadata.ownerReferences)' \
	| jq -r '.metadata.namespace="'${NSTGT}'"' \
	 | oc apply --namespace=$NSTGT -f -
   # do the same with secret for the Kafka cluster ca cert. SECRET=dev-cluster-ca-cert  
  ```

* Build and deploy

```sh
mvn clean package -Dquarkus.container-image.build=true -Dquarkus.kubernetes.deploy=true -DskipTests
```
* Get the application route: `oc get route da-qs-order-ms`
* Send one order via the POST orders end point:

```sh
 {  "customerID": "C01",
    "productID": "P02",
    "quantity": 15,
    "destinationAddress": {
      "street": "12 main street",
      "city": "san francisco",
      "country": "USA",
      "state": "CA",
      "zipcode": "92000"
    }
}
```

* Verify messages are published to the topic, by getting to the Event Streams console.
