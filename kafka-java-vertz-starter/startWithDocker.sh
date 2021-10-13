docker run -ti -p 8080:8080 \
-v  $(pwd)/kafka.properties:/deployments/kafka.properties \
-v  $(pwd)/truststore.p12:/deployments/truststore.p12 \
ibmcase/es-demo