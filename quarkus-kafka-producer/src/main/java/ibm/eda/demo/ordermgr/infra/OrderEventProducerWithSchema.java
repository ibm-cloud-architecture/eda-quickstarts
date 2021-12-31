package ibm.eda.demo.ordermgr.infra;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jboss.logging.Logger;

import ibm.eda.demo.ordermgr.infra.events.EventEmitter;
import ibm.eda.demo.ordermgr.infra.events.OrderEvent;
import io.apicurio.registry.client.RegistryRestClient;
import io.apicurio.registry.client.RegistryRestClientFactory;
import io.apicurio.registry.rest.beans.ArtifactMetaData;
import io.apicurio.registry.rest.beans.IfExistsType;
import io.apicurio.registry.types.ArtifactType;

/**
 * Avro and schema registry based Kafka producer.
 */
@ApplicationScoped
@Named("avroProducer")
public class OrderEventProducerWithSchema implements EventEmitter {
    Logger logger = Logger.getLogger(OrderEventProducerWithSchema.class.getName());

    private KafkaProducer<String,OrderEvent> kafkaProducer = null;
    private KafkaWithSchemaRegistryConfiguration configuration = null;
    private Schema avroSchema;
   

    public OrderEventProducerWithSchema() {
        super();
        configuration = new KafkaWithSchemaRegistryConfiguration();
        
        Properties props = configuration.getProducerPropertiesWithSchemaRegistry();
        kafkaProducer = new KafkaProducer<String, OrderEvent>(props);
        try {
            Map<String,Object> config = (Map)props; 
            RegistryRestClient client = RegistryRestClientFactory.create(configuration.REGISTRY_URL, config);
            
            //avroSchema =  new Schema.Parser().parse(client.getLatestArtifact(configuration.groupId,configuration.artifactId));
            Schema.Parser schemaDefinitionParser = new Schema.Parser();
            avroSchema = schemaDefinitionParser.parse(new File(configuration.getSchemaPath()));
            logger.info(avroSchema.toString());
            InputStream content = new ByteArrayInputStream(avroSchema.toString().getBytes(StandardCharsets.UTF_8));
            ArtifactMetaData metaData = client.createArtifact(configuration.getArtifactId(), ArtifactType.AVRO, IfExistsType.RETURN, content);
           logger.info(metaData.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }

    public void connectApicurio232(){
        /**
         * try {
            Map<String,Object> config = (Map)props; 
            RegistryClient client = RegistryClientFactory.create(configuration.REGISTRY_URL, config);
            
            //avroSchema =  new Schema.Parser().parse(client.getLatestArtifact(configuration.groupId,configuration.artifactId));
            Schema.Parser schemaDefinitionParser = new Schema.Parser();
            avroSchema = schemaDefinitionParser.parse(new File(configuration.getSchemaPath()));
            logger.info(avroSchema.toString());
            InputStream content = new ByteArrayInputStream(avroSchema.toString().getBytes(StandardCharsets.UTF_8));
            ArtifactMetaData metaData = client.createArtifact(configuration.getGroupId(), configuration.getArtifactId(), ArtifactType.AVRO,content);
           logger.info(metaData.toString());
        } catch(Exception e) {
            e.printStackTrace();
        }
         */
        
    }

    public void sendOrderEvents(List<OrderEvent> l) {
        for (OrderEvent t : l) {
            emit(t);
        }
    }

    public void emit(OrderEvent oevent) { 
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
