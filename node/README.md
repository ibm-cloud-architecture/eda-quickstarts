This is quick starter code for kafka application development with kafka.

`To run this application first install npm`
```bash
npm install
```

Copy ``env.temp.sh`` file as env.sh 

fill all required property in env.sh file

```
export KAFKA_BOOTSTRAP_SERVERS=# broker url
export SASL_MECHANISM=# mechanish used 
export KAFKA_SASL_USERNAME=# username
export KAFKA_SASL_PASSWORD=# password
export KAFKA_SASL_TRUSTSTORE_LOCATION=# pem file
export KAFKA_TOPIC=# topic name
```

Source environment file
```bash
source env.sh
```

To Produce message to your kafka topic run
```bash
node producer.js
```

To Consume message from kafka topic run
```bash
node consumer.js
```