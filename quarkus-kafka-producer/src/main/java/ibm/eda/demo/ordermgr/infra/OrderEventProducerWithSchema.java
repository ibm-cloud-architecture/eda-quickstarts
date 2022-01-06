package ibm.eda.demo.ordermgr.infra;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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

    protected KafkaProducer<String,OrderEvent> kafkaProducer = null;
    @Inject
    protected KafkaConfiguration configuration;
    protected Schema avroSchema;
   

    public OrderEventProducerWithSchema() {
        super();   
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
       
        logger.info("sending to " + getConfiguration().getTopicName() + " item " + producerRecord
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

    

    public KafkaConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void init() {
        Properties props = getConfiguration().getProducerProperties();
        kafkaProducer = new KafkaProducer<String, OrderEvent>(props);
        try {
            Map<String,Object> config = (Map)props; 
            RegistryRestClient client = RegistryRestClientFactory.create(getConfiguration().getRegistryURL(), config);
            Schema.Parser schemaDefinitionParser = new Schema.Parser();
            if (getConfiguration().getSchemaPath() != null) {
                avroSchema = schemaDefinitionParser.parse(new File(getConfiguration().getSchemaPath()));
                logger.info("@@@ Load schema from file " + getConfiguration().getSchemaPath() + "\n" + avroSchema.toString());
                InputStream content = new ByteArrayInputStream(avroSchema.toString().getBytes(StandardCharsets.UTF_8));
                ArtifactMetaData metaData = client.createArtifact(getConfiguration().getArtifactId(), ArtifactType.AVRO, IfExistsType.RETURN, content);
               logger.info(metaData.toString());
            } else {
                logger.info("@@@ load schema from registry using " + getConfiguration().getArtifactId());
                avroSchema =  new Schema.Parser().parse(client.getLatestArtifact(getConfiguration().getArtifactId()));
            } 
           
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
