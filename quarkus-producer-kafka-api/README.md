# Quarkus app for producing event to Kafka via API


## Running the application in dev mode

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

* Go to the swagger UI: [http://localhost:8080/q/swagger-ui/](http://localhost:8080/q/swagger-ui/) or use
the following calls to get the connection to Kafka started:

```sh
curl -X 'GET' 'http://localhost:8080/api/v1/orders' -H 'accept: application/json'
```

* Go to Kafdrop console [http://localhost:9000/](http://localhost:9000/) to see messages in topic.


> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```
The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

We have also done a `scripts/buildAll` that you can reuse to compile, package the quarkus app,
build a docker image and push it to a registry.

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

### Deploy using public image and yamls

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

## Integrating with GitOps

In the `kustomize` folder we have defined configmap, deployment,... that you can reuse to
deploy your app to OpenShift. Doing an `oc apply -k kustomize` will deploy the current
template to an OpenShift project.

If you use OpenShift GitOps to deploy your solution, you can create your GitOps project with the kam CLI 
and then create a folder in the `environment/dev/apps` with the name
of your app based on this code, then copy the `kustomize` folder content under this newly
created folder. After that you need to add an Argocd app under the `config` folder.


## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

