import os,sys
from kafka.KcConsumer import KafkaConsumer

print(" @@@ Executing script: ConsumePlainMessage.py")

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


####################### FUNCTIONS #######################
# Parse arguments to get the Kafka topic
def parseArguments():
    global TOPIC_NAME
    print("The arguments for this script are: " , str(sys.argv))
    if len(sys.argv) == 2:
        TOPIC_NAME = sys.argv[1]
    else:
        print("[ERROR] - The ConsumePlainMessage.py script expects one argument: The Kafka topic to consume messages from")
        exit(1)

####################### MAIN #######################
if __name__ == '__main__':
    # Parse arguments to get the topic to read from
    parseArguments()
    # Create a Kafka Consumer
    kafka_consumer = KafkaConsumer(KAFKA_BROKERS,SCRAM_USERNAME,SCRAM_PASSWORD,TOPIC_NAME)
    # Prespare the consumer
    kafka_consumer.prepareConsumer()
    # Poll for next message
    kafka_consumer.pollNextEvent()
    # Close the consumer
    kafka_consumer.close()