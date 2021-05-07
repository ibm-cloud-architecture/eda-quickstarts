package ibm.eda.demo.app.infrastructure;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jboss.logging.Logger;

import ibm.eda.demo.app.infrastructure.events.EventEmitter;
import ibm.eda.demo.app.infrastructure.events.OrderEvent;
import io.apicurio.registry.rest.client.RegistryClient;
import io.apicurio.registry.rest.client.RegistryClientFactory;

/**
 * Avro and schema registry based Kafka producer.
 */
@Singleton
@Named("avroProducer")
public class OrderEventAvroProducer implements EventEmitter {
    Logger logger = Logger.getLogger(OrderEventAvroProducer.class.getName());

    private KafkaProducer<String,OrderEvent> kafkaProducer = null;
    private KafkaWithSchemaRegistryConfiguration configuration = null;
    private Schema avroSchema;
   

    public OrderEventAvroProducer() {
        super();
        configuration = new KafkaWithSchemaRegistryConfiguration();
        
        Properties props = configuration.getAvroProducerProperties("OrderProducer_1");
        kafkaProducer = new KafkaProducer<String, OrderEvent>(props);
        try {
            Map<String,Object> config = (Map)props; 
            RegistryClient client = RegistryClientFactory.create(configuration.REGISTRY_URL, config);
            avroSchema =  new Schema.Parser().parse(client.getLatestArtifact(configuration.groupId,configuration.artifactId));
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }

    public void sendOrderEvents(List<OrderEvent> l) {
        for (OrderEvent t : l) {
            emit(t);
        }
    }

    public void emit(OrderEvent oevent) { 
        logger.info(avroSchema.toString());
        GenericRecord record = new GenericData.Record(avroSchema);
        ProducerRecord<String, OrderEvent> producerRecord = new ProducerRecord<String, OrderEvent>(
                configuration.getTopicName(), oevent.getOrderID(), oevent);
       
        logger.info("sending to " + configuration.getTopicName() + " item " + producerRecord
        .toString());
        try {
            this.kafkaProducer.send(producerRecord, new Callback() {

                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        logger.info("The offset of the record just sent is: " + metadata.offset());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            safeClose();
        }
        // logger.info("Partition:" + resp.partition());
    }

    
    @Override
    public void safeClose(){
        kafkaProducer.flush();
        kafkaProducer.close();
        kafkaProducer = null;
    }
}
