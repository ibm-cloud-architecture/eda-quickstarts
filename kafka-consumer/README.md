# kafka-consumer project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

Here we have quick assets to make kafka applicaiton

to connect to kafka set all required property from ibm cloud to env.sh
then do 
```bash
source env.sh
```

After that you can do s
```bash
./mvnw quarkus:dev
```

Code to consume message from kafka using `src/java/tsa/eda/app/Consumer.java`