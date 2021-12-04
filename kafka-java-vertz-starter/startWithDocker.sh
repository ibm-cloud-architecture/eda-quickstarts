docker run -ti -p 8080:8080 \
-v  $(pwd)/kafka.properties:/deployments/kafka.properties \
-v  $(pwd)/es-cert.p12:/deployments/es-cert.p12 \
quay.io/ibmcase/es-demo