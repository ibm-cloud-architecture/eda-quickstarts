# Setting up your environment

Welcome to your environment setup!

The steps below will guide you towards a successful deployment of a vanilla Kafka cluster and the necessary configurations that will ensure that producers and consumers communicate with it in a secure manner.

**Prerequisites**: \
[RedHat OpenShift 4.6+](https://docs.openshift.com/container-platform/4.8/welcome/index.html) \
[Strimzi Operator](https://strimzi.io/documentation/) (deploy and manage Kafka cluster) \
OpenShift CLI (execute `oc` commands)

## Installing & configuring your Kafka cluster

Deploying Kafka cluster with internal connection only
```
oc create -f environment/kafka/strimzi/kafka-cluster-internal.yaml
```

Deploying Kafka cluster with exposed bootstrap server (NodePort)
```
oc create -f environment/kafka/strimzi/kafka-cluster-external.yaml
```

Deploying Kafka TLS-User
```
oc create -f environment/kafka/strimzi/kafka-tls-user.yaml
```

## Installing Apicurio Schema Registry

Deploying Apicurio Schema Registry instance in the same namespace as the Kafka cluster
```
oc create -f environment/apicurio/2.1.5/registry-kafkasql-2.1.5.Final.yaml
```


## Producer / Consumer configmap
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

```
oc create -f environment/prodcon/configmap.yaml
```

## Managing certificates

### Extracting secrets

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
