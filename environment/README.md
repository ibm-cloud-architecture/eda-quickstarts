# Setting up your environment

Welcome to your environment setup!

The steps below will guide you towards a successful deployment of a vanilla Kafka cluster and the necessary configurations that will ensure that producers and consumers communicate with it in a secure manner.

**Table of Contents**  
[Prerequisites](#prerequisites)  
[Step 1 - Installing & configuring your Kafka cluster](#step-1---installing--configuring-your-kafka-cluster)  
[Step 2 - Installing Apicurio Schema Registry](#step-2---installing-apicurio-schema-registry)  
[Step 3 - Producer / Consumer configmap](#step-3---producer--consumer-configmap)  
[Step 4 - Managing certificates](#step-4---managing-certificates)  


#### **Prerequisites**:  
[RedHat OpenShift (OCP) 4.6+](https://docs.openshift.com/container-platform/4.8/welcome/index.html)  
[Strimzi Operator](https://strimzi.io/documentation/) (deploy and manage Kafka cluster)  
OpenShift CLI (execute `oc` commands)

## Step 1 - Installing & configuring your Kafka cluster

### Deploying Kafka cluster

*If you are going to deploy your producers & consumers in the same OCP environment as the Kafka cluster use the internal deployment scenario*
```
oc create -f environment/kafka/strimzi/kafka-cluster-internal.yaml
```

*If you are going to deploy your producers & consumers in a separate environment use the external deployment scenario*
```
oc create -f environment/kafka/strimzi/kafka-cluster-external.yaml
```

### Creating Kafka User
```
oc create -f environment/kafka/strimzi/kafka-tls-user.yaml
```

## Step 2 - Installing Apicurio Schema Registry

*Deploy your Apicurio Schema Registry instance in the same namespace as the Kafka cluster*
```
oc create -f environment/apicurio/2.1.5/registry-kafkasql-2.1.5.Final.yaml
```

## Step 3 - Producer / Consumer configmap
Producers and consumers need to know the adress of the bootstrap server and the Apicurio Schema Registry. You can retrieve those by running the commands below. Once you have them you can update the `configmap` in `environment/prodcon/configmap.yaml` and create it using the command below.

### Retrieving Bootstrap server address

```
oc get kafka my-kafka -o=jsonpath='{.status.listeners[?(@.type=="external")].bootstrapServers}{"\n"}'
```

### Retrieving Apicurio Schema Registry address

```
oc get route/my-apicurioregistry-kafkasql-service -o=jsonpath='{.spec.host}{"\n"}'
```

### Create configmap
*Create this configmap in the environment/namespace where producers/consumers are being deployed*
```
oc create -f environment/prodcon/configmap.yaml
```

## Step 4 - Managing certificates

*Complete this step if you are deploying producers/consumers in a different namespace or OCP cluster than your Kafka deployment*

### Extracting secrets from Kafka cluster environment

Cluster certificate

```
oc extract secret/my-kafka-cluster-ca-cert --keys=ca.p12,ca.password
```

User certificate

```
oc extract secret/tls-user --keys=user.p12,user.password
```

### Creating secrets in producer/consumer environments

Cluster certificate
```
oc create secret generic my-kafka-cluster-ca-cert --from-file=ca.p12 --from-file=ca.password
```

User certificate
```
oc create secret generic tls-user --from-file=user.p12 --from-file=user.password
```
