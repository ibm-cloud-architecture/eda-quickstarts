package ibm.eda.demo.ordermgr.infra.kafka;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.WebApplicationException;

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
public class OrderEventProducer implements EventEmitter {
    Logger logger = Logger.getLogger(OrderEventProducer.class.getName());

    protected KafkaProducer<String,OrderEvent> kafkaProducer = null;
    @Inject
    protected KafkaConfiguration configuration;
    protected Schema avroSchema;
   

    public OrderEventProducer() {
        super();   
    }

    public OrderEventProducer(KafkaConfiguration configuration2) {
        this.configuration = configuration2;
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
            getConfiguration().getTopicName(), oevent.getOrderID(), oevent);
        
        logger.info("sending to " + getConfiguration().getTopicName() + " topic, the record: " + producerRecord
        .toString());
        this.kafkaProducer.send(producerRecord, 
                (metadata, exception) -> {
                    if (exception != null) {
                        logger.error(exception);
                    } else {
                        logger.info("The offset of the record just sent is: " + metadata.offset());
                    }
                }
            );
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
            RegistryRestClient registryClient = RegistryRestClientFactory.create(getConfiguration().getRegistryURL(), config);
            avroSchema = OrderEvent.SCHEMA$;
            try {
                    ArtifactMetaData metaData = registryClient.getArtifactMetaData(getConfiguration().getArtifactId());
                    logger.info("Schema already in registry: " + metaData.toString());
            } catch (WebApplicationException e) {
                    InputStream content = new ByteArrayInputStream(avroSchema.toString().getBytes(StandardCharsets.UTF_8));
                    ArtifactMetaData metaData = 
                     registryClient.createArtifact(getConfiguration().getArtifactId(), ArtifactType.AVRO, IfExistsType.RETURN, content);
                    logger.info("Schema created in registry:" + metaData.toString());
            }       
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
}
