# Cloud Object Storage Sink tutorial Project

The Java Quarkus app is a generator for order events with a simple PLAINTEXT connection to
IBM Event Streams to be source of Cloud Object Storage.

## Development history

The project was created with the quarkus CLI:

```sh
quarkus create app cos-tutorial 
cd cos-tutorial
quarkus ext add  reactive-messaging-kafka, mutiny, openshift
```

Then DemoController.java exposes two end points and use reactive messaging to produce
random Order.

For OpenShift deployment the configuration is controlled via configuration map, kustomize.

## Run locally

for development purpose:

```sh
quarkus dev
```

Go to the swagger UI to start the demo:

```sh
curl -X 'POST' \
  'http://localhost:8080/api/v1/start' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '10'
```

## Deploy on OpenShift

* continuously deploy on OpenShift while developing

```sh
./mvnw package -Dquarkus.container-image.build=true -Dquarkus.kubernetes.deploy=true
```

 Then get the routes for the `eda-code-demo` and use the swagger ui to start the demo.

* Adopting a partial Gitops approach: 

   * Build the image and push it to quay.io: `scripts/buildAdd.sh`. Update the image name to use your own registry.
   * Deploy with the kustomize: `oc apply -k kustomize/`
   * The service, configmap, event stream topic, and app deployment resources are created, which means
   this `kustomize` folder could be copied in a GitOps folder created by kam.