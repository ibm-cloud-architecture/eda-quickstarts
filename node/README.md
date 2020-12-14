This is quick starter code for kafka application development with kafka.

`To run this application first install npm`
```bash
npm install
```

`Copy ``env.temp.sh`` file as env.sh. 
fill all required property in env.sh file`
```
export BROKER= # broker url
export MECHANISM= # mechanish used 
export USERNAME= # username
export PASSWORD= # password
export CERT= # pem file
export TOPIC= # topic name
```

Source environment file
```bash
source env.sh
```

`To Produce message to your kafka topic run`
```bash
npm producer.js
```

`To Consume message from kafka topic run`
```bash
npm comsumer.js
```