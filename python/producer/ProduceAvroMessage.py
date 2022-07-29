import os, time, sys, json
from kafka.KcAvroProducer import KafkaProducer
from avro_files.utils.avroEDAUtils import *

print(" @@@ Executing script: ProduceAvroMessage.py")

####################### READ ENV VARIABLES #######################
# Try to read the Kafka broker from the environment variables
try:
    KAFKA_BROKERS = os.environ['KAFKA_BROKERS']
except KeyError:
    print("[ERROR] - The KAFKA_BROKERS environment variable needs to be set.")
    exit(1)

# Try to read the SCRAM username from the environment variables
try:
    SCRAM_USERNAME = os.environ['SCRAM_USERNAME']
except KeyError:
    print("[ERROR] - The SCRAM_USERNAME environment variable needs to be set")
    exit(1)

# Try to read the SCRAM password from the environment variables
try:
    SCRAM_PASSWORD = os.environ['SCRAM_PASSWORD']
except KeyError:
    print("[ERROR] - The SCRAM_PASSWORD environment variable needs to be set")
    exit(1)

# Try to read the schema registry url from the environment variables
try:
    SCHEMA_REGISTRY_URL = os.environ['SCHEMA_REGISTRY_URL']
except KeyError:
    print("[ERROR] - The SCHEMA_REGISTRY_URL environment variable needs to be set.")
    exit(1)

####################### VARIABLES #######################
DATA_SCHEMAS=os.getcwd() + "/../avro_files"

####################### Functions #######################
# Create a default event
def createEvent():
    print('Creating event...')
    
    key = {"key": 1}
    value = {"message" : "This is a test message"}
    
    print("DONE")
    
    return json.dumps(value), json.dumps(key)

# Parse arguments to get the kafka topic name
def parseArguments():
    global TOPIC_NAME
    print("The arguments for the script are: " , str(sys.argv))
    if len(sys.argv) == 2:
        TOPIC_NAME = sys.argv[1]
    else:
        print("[ERROR] - The ProduceAvroMessage.py script expects one arguments: The Kafka topic to send the event to.")
        exit(1)


####################### MAIN #######################
if __name__ == '__main__':
    # Get the Kafka topic name
    parseArguments()
    # Get the avro schemas for the message's key and value
    event_value_schema = getDefaultEventValueSchema(DATA_SCHEMAS)
    event_key_schema = getDefaultEventKeySchema(DATA_SCHEMAS)
    # Create the event
    event_value, event_key = createEvent()
    # Print out the event to be sent
    print("--- Event to be published: ---")
    print(event_key)
    print(event_value)
    print("----------------------------------------")
    # Create the Kafka Avro Producer
    kafka_producer = KafkaProducer(KAFKA_BROKERS,SCRAM_USERNAME,SCRAM_PASSWORD,SCHEMA_REGISTRY_URL)
    # Prepare the Kafka Avro Producer
    kafka_producer.prepareProducer("ProduceAvroMessagePython",event_key_schema,event_value_schema)
    # Publish the event
    kafka_producer.publishEvent(TOPIC_NAME,event_value,event_key)
