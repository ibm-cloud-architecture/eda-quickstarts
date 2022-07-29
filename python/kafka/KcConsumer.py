import json,os
from confluent_kafka import Consumer, KafkaError


class KafkaConsumer:

    def __init__(self, kafka_brokers = "", scram_username = "",scram_password = "", topic_name = "",autocommit = True):
        self.kafka_brokers = kafka_brokers
        self.scram_username = scram_username
        self.scram_password = scram_password
        self.topic_name = topic_name
        self.kafka_auto_commit = autocommit

    # See https://github.com/edenhill/librdkafka/blob/master/CONFIGURATION.md
    # Prepares de Consumer with specific options based on the case
    def prepareConsumer(self, groupID = "ConsumePlainMessagePython"):
        options ={
                'bootstrap.servers':  self.kafka_brokers,
                'group.id': groupID,
                'auto.offset.reset': 'earliest',
                'enable.auto.commit': self.kafka_auto_commit,
                'security.protocol': 'SASL_SSL',
                'sasl.mechanisms': 'SCRAM-SHA-512',
                'sasl.username': self.scram_username,
                'sasl.password': self.scram_password,
                'ssl.ca.location': os.environ['PEM_CERT']
        }
        # Print the configuration
        print("--- This is the configuration for the consumer: ---")
        print(options)
        print("---------------------------------------------------")
        # Create the consumer
        self.consumer = Consumer(options)
        # Subscribe to the topic
        self.consumer.subscribe([self.topic_name])
    
    # Prints out and returns the decoded events received by the consumer
    def traceResponse(self, msg):
        msgStr = msg.value().decode('utf-8')
        print('[Message] - Next Message consumed from {} partition: [{}] at offset {} with key {}:\n\tmessage: {}'
                    .format(msg.topic(), msg.partition(), msg.offset(), str(msg.key()), msgStr ))

    # Polls for next event
    def pollNextEvent(self):
        # Poll for messages
        msg = self.consumer.poll(timeout=10.0)
        # Validate the returned message
        if msg is None:
            print("[INFO] - No new messages on the topic")
        elif msg.error():
            if ("PARTITION_EOF" in msg.error()):
                print("[INFO] - End of partition")
            else:
                print("[ERROR] - Consumer error: {}".format(msg.error()))
        else:
            # Print the message
            msgStr = self.traceResponse(msg)
    
    def close(self):
        self.consumer.close()