# kafka-consumer project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

Here we have quick assets to make kafka applicaiton

to connect to kafka set all required property from ibm cloud to env.sh
then do 
```bash
source env.sh
```
After that you can do 
```bash
./mvnw quarkus:dev
```

Code to produce message to kafka is located in `src/java/tsa/eda/app/Producer.java`

Code to consume message from kafka using `src/java/tsa/eda/app/Consumer.java`

there is also endpoint to send message to kafka. localhost:8082/kafka/produce
