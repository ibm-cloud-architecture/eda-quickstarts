# Quarkus app for producing event to Kafka via API


## Running the application in dev mode

you must start Kafka with docker compose using the compose file under `../environment/local/strimzi`:

```sh
docker compose up -d
```

You can run your application in dev mode that enables live coding using:

```shell script
quarkus dev
```

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

* Create a OpenShift project
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

