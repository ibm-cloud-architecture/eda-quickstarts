import json, os
from confluent_kafka import KafkaError, Producer

class KafkaProducer:

    def __init__(self,kafka_brokers = "",scram_username = "",scram_password = ""):
        self.kafka_brokers = kafka_brokers
        self.scram_username = scram_username
        self.scram_password = scram_password

    def prepareProducer(self,groupID = "pythonproducers"):
        # Configure the Kafka Producer (https://docs.confluent.io/current/clients/confluent-kafka-python/#kafka-client-configuration)
        options ={
                'bootstrap.servers':  self.kafka_brokers,
                'group.id': groupID,
                'security.protocol': 'SASL_SSL',
                'sasl.mechanisms': 'SCRAM-SHA-512',
                'sasl.username': self.scram_username,
                'sasl.password': self.scram_password,
                'ssl.ca.location': os.environ['PEM_CERT']
        }
        # Print out the configuration
        print("--- This is the configuration for the producer: ---")
        print(options)
        print("---------------------------------------------------")
        # Create the producer
        self.producer = Producer(options)

    def delivery_report(self,err, msg):
        """ Called once for each message produced to indicate delivery result. Triggered by poll() or flush(). """
        if err is not None:
            print('[ERROR] - Message delivery failed: {}'.format(err))
        else:
            print('Message delivered to {} [{}]'.format(msg.topic(), msg.partition()))

    def publishEvent(self, topicName, eventToSend, keyName):
        # Print the event to send
        dataStr = json.dumps(eventToSend)
        # Produce the message
        self.producer.produce(topicName,key=eventToSend[keyName],value=dataStr.encode('utf-8'), callback=self.delivery_report)
        # Flush
        self.producer.flush()