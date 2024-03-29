# Cloud Object Storage Sink tutorial Project

## Overview

This folder includes code and instructions to demonstrate how to integrate Kafka topic to a sink connector to cloud object storage. 
The src folder defines a Java Quarkus app used to generate order events with a simple PLAINTEXT connection to 
IBM Event Streams brokers and gitOps/services/kconnect folder includes the  Cloud Object Storage sink kafka connector configuration and image build.

## Audiance

Developer, architect.

## Pre-requisite

To run on OpenShift, you need to login as cluster administrator and use and existing deployed  Event Streams cluster. [See this tutorial]() to do so or use [the Deploy on OpenShift section below](#deploy-on-openshift).


## What is inside

The project was created with the quarkus CLI:

```sh
quarkus create app cos-tutorial 
cd cos-tutorial
quarkus ext add  reactive-messaging-kafka, mutiny, openshift
```

Then `DemoController.java` exposes two end points and uses reactive messaging to produce
random Sale Transaction in a light version of a [TLOG](https://github.com/DFDLSchemas/IBM4690-TLOG). It is wrapped into a [CloudEvent](https://cloudevents.io/)
using Quarkus reactive messaging.

For OpenShift deployment, the configuration is controlled via configuration map, and kustomization defined under `gitOps` folder.

### Build and package the demo app

Modify the following script to change the registry and account as needed.

```sh
./scripts/buildAll.sh
```

### Build and package the kafka connector app

*  build

```sh
docker build -t quay.io/ibmcase/eda-kconnect-cluster-image .
```

* Verify jars are in

```sh
docker run -ti quay.io/ibmcase/eda-kconnect-cluster-image   bash -c "ls /opt/kafka/plugins"
```

## Run locally

For pure development purpose, you can continuously develop and run the application with run with `quarkus dev`, which starts a local Kafka broker (RedPanda):

```sh
quarkus dev
```

* Go to the swagger UI http://localhost:8080/q/swagger-ui to start the demo and use the `/api/v1/start` API:

![](docs/swagger-ui.png)

* If you want to verify the generated messages are in the kafka topic inside RedPanda use the `rpk` CLI, inside
redpanda 

```sh
CID=$(docker ps | grep redpanda | awk '{print $1}' )
docker exec $CID bash -c "rpk topic  consume orders -o start --pretty-print"
```

### With docker compose

We also have defined a docker compose file to demonstrate this end to end scenario on your laptop.

As a pre-requisite you need an IBM Cloud Object Storage service provisioned. See the
[Create an IBM COS Service and COS Bucket article](https://ibm-cloud-architecture.github.io/refarch-eda/use-cases/connect-cos/#create-an-ibm-cos-service-and-cos-bucket).

Be sure to have docker daemon running and docker-compose cli installed.

* Modify the file `local-kconnect/tmp-kafka-cos-sink-standalone.properties` and rename it as kafka-cos-sink-standalone.properties`
with the properties to connect to your own IBM Cloud Object Storage service:

```sh
cos.api.key=IBM_COS_API_KEY
cos.bucket.location=IBM_COS_BUCKET_LOCATION
cos.bucket.name=IBM_COS_BUCKET_NAME
cos.bucket.resiliency=IBM_COS_RESILIENCY
cos.service.crn="crn:IBM_COS_CRM"
```

* Start the environment

```sh
docker-compose up -d
```

* Create the `orders` topic

```sh
./scripts/createTopics.sh 
./scripts/listTopics.sh 
```

* Connect to Kafdrop to see the topic content:

```sh
chrome http://localhost:9000/topic/orders
```

* Start to send some TLOG orders

```sh
curl -X 'POST' \
  'http://localhost:8080/api/v1/start' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '10'
```

* Go to the Cloud Object Storage user interface and the `eda-demo`, you should see new record added.

![](docs/cos-eda-demo-bucket.png)

You can download those records or use SQL query to access them.

* Stop everything:

```sh
docker-compose down
```

## Deploy on OpenShift

As a pre-requisite you need one instance of Event Streams. The gitOps folder includes
the simplest event streams cluster needed. 

* Create an OpenShift project (k8s namespace) named: `eda-cos`

  ```sh
  oc apply -k gitOps/env/base
  ```

* Using [IBM entitled registry entitlement key](https://www.ibm.com/docs/en/cloud-paks/cp-integration/2020.2?topic=installation-entitled-registry-entitlement-keys) 
 define your a secret so deployment process can download IBM Event Streams images:

```sh
KEY=<yourentitlementkey>
oc create secret docker-registry ibm-entitlement-key \
    --docker-username=cp \
    --docker-password=$KEY \
    --docker-server=cp.icr.io \
    --namespace=eda-cos
```

* Deploy Event Streams Cluster.
 
 ```sh
 oc apply -k gitOps/services/es 
 # --> Results
 eventstreams.eventstreams.ibm.com/dev created
 kafkatopic.eventstreams.ibm.com/edademo-orders created
 ```

It will take sometime to get the cluster created. Monitor with `oc get pod -w`. You should
get:

```
dev-entity-operator-6d7d94f68f-6lk86   3/3    
dev-ibm-es-admapi-ffd89fdf-x99lq       1/1    
dev-ibm-es-ui-74bf84dc67-qx9kk         2/2    
dev-kafka-0                            1/1    
dev-zookeeper-0                        1/1
```

With this deployment there is no external route, only on bootstrap URL: `dev-kafka-bootstrap.eda-cos.svc:9092`. The Kafka listener is using PLAINTEXT connection. So no SSL encryption and no authentication.

* Deploy the existing application (the image we built is in quay.io/ibmcase) using:

```sh
oc apply -k gitOps/apps/eda-cos-demo/base/
```

* Test by going to the route and the swagger UI and post 5 or 10 messages. The messages should be visible in the `edademo-orders` queue.
Another way is to use curl and replace with your own deployment:

  ```sh
  URL='http://eda-cos-demo-eda-cos.cp4i-es-mq-1e......containers.appdomain.cloud/api/v1/start'
  curl -X 'POST' \
   $URL \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '10'
  ```

* As another way to deploy the app, you can use quarkus deployment capability:

```sh
# Be sure to have created the configmap
oc apply -f gitOps/apps/eda-cos-demo/base/configmap.yaml 
./mvnw package -Dquarkus.container-image.build=true -Dquarkus.kubernetes.deploy=true
```

Then get the routes for the `eda-code-demo` and use the swagger ui to start the demo.

* If you plan to use a Gitops approach you can: 

   * Build the image and push it to quay.io: `scripts/buildAll.sh`. Update the image name to use your own registry.
   * The service, configmap, event stream topic, and app deployment resources are created, which means
   this `gitOps/apps` folder could be copied in a GitOps folder created by kam.

## Deploy Kafka connect

We have already build a kafka connect image with the Cloud Object Storage jar so normally
to deploy to the `eda-cos` we need to just do:

```sh
oc apply -f gitOps/services/kconnect/kafka-connect.yaml
# Verify cluster is ready
oc get kafkaconnect
```

## Deploy the cos sink connector

Modify the `kafka-cos-sink-connector.yaml` with your Cloud Object Storage credentials, URL, bucket...

then deploy the connector:

```sh
oc apply -f gitOps/services/kconnect/kafka-cos-sink-connector.yaml
```

You can verify the connector is running by going to the connect instance

```sh
oc get pods | grep kconnect
oc exec -ti <podid> bash
# in the pod
curl localhost:8083/connectors
```

## Verify in COS the replicated records

![](docs/cos-eda-demo-bucket.png)

### For the maintainer of this tutorial

Here are the needed steps to follow as of 2/22/2022.

* Maintainance of the kafka connect connector in the `src/main/kconnect` folder.

```sh
# clone source 
git clone https://github.com/ibm-messaging/kafka-connect-ibmcos-sink
cd kafka-connect-ibmcos-sink
# Be sure to be on java 11 or 8 to avoid a class version exception
sdk list java
sdk use  java 8.0.292.j9-adpt 
gradle shadowJar
# mv create jars to plugin
mv build/libs/kafka-connect-ibmcos-sink-1.0.0-all.jar src/main/kconnect/my-plugins
# Build the docker image
docker build -t quay.io/ibmcase/eda-cos-kconnect-cluster-image .
docker push quay.io/ibmcase/eda-cos-kconnect-cluster-image
```
