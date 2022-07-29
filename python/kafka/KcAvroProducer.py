import json,os
from confluent_kafka import KafkaError
from confluent_kafka.avro import AvroProducer

class KafkaProducer:

    def __init__(self,kafka_brokers = "",scram_username = "",scram_password = "",schema_registry_url = ""):
        self.kafka_brokers = kafka_brokers
        self.scram_username = scram_username
        self.scram_password = scram_password
        self.schema_registry_url = schema_registry_url

    def prepareProducer(self,groupID = "pythonproducers",key_schema = "", value_schema = ""):
        options ={
                'bootstrap.servers':  self.kafka_brokers,
                'schema.registry.url': self.schema_registry_url,
                'group.id': groupID,
                'security.protocol': 'SASL_SSL',
                'sasl.mechanisms': 'SCRAM-SHA-512',
                'sasl.username': self.scram_username,
                'sasl.password': self.scram_password,
                'ssl.ca.location': os.environ['PEM_CERT'],
                'schema.registry.ssl.ca.location': os.environ['PEM_CERT']
        }
        # Print out the configuration
        print("--- This is the configuration for the avro producer: ---")
        print(options)
        print("---------------------------------------------------")
        # Create the Avro Producer
        self.producer = AvroProducer(options,default_key_schema=key_schema,default_value_schema=value_schema)

    def delivery_report(self,err, msg):
        """ Called once for each message produced to indicate delivery result. Triggered by poll() or flush(). """
        if err is not None:
            print('[ERROR] - Message delivery failed: {}'.format(err))
        else:
            print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))

    def publishEvent(self, topicName, value, key):
        # Produce the Avro message
        # Important: value DOES NOT come in JSON format from ContainerAvroProducer.py. Therefore, we must convert it to JSON format first
        #self.producer.produce(topic=topicName,value=json.loads(value),key=json.loads(value)[key], callback=self.delivery_report)
        self.producer.produce(topic=topicName,value=json.loads(value),key=json.loads(key), callback=self.delivery_report)
        # Flush
        self.producer.flush()