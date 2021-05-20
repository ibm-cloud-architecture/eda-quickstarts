package ibm.eda.demo.ordermgr.infra;

import java.util.Optional;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.eclipse.microprofile.config.ConfigProvider;

import io.apicurio.registry.serde.SerdeConfig;
import io.apicurio.registry.serde.avro.AvroKafkaSerializer;
import io.apicurio.registry.serde.avro.strategy.TopicRecordIdStrategy;
import io.apicurio.registry.serde.avro.strategy.RecordIdStrategy;
import io.apicurio.registry.serde.avro.AvroSerde;
/**
 * Centralize in one class the Kafka Configuration when using schema registry. Useful when app has producer
 * and consumer
 */
public class KafkaWithSchemaRegistryConfiguration  extends KafkaConfiguration {
   
    public String REGISTRY_URL="http://localhost:8090/";

    public String groupId = "OrderGroup";
    public String artifactId = "OrderEvent";

    public  Properties getAvroProducerProperties(String clientID) {
        Properties properties = getProducerProperties(clientID);
        
        
        Optional<String> vs = ConfigProvider.getConfig().getOptionalValue("kafka.key.serializer", String.class);
        if (vs.isPresent()) {
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, vs.get());
        } else {
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        }

        vs = ConfigProvider.getConfig().getOptionalValue("kafka.value.serializer", String.class);
        if (vs.isPresent()) {
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, vs.get());
        } else {
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        }
     
        vs = ConfigProvider.getConfig().getOptionalValue("kafka.schema.registry.url", String.class);
        if (vs.isPresent()) {
            // Apicurio settings
            properties.putIfAbsent(SerdeConfig.REGISTRY_URL, vs.get());
            REGISTRY_URL=vs.get();
            if (! truststoreLocation.isEmpty()) {
                properties.put("value.converter.schema.registry.ssl.trutstore", truststoreLocation);
                properties.put("value.converter.schema.registry.ssl.trutstore.password",truststorePassword);
                properties.put("schema.registry.ssl.truststore.location",truststoreLocation);
                properties.put("schema.registry.ssl.truststore.password",truststorePassword);
            }
        } else {
            properties.putIfAbsent(SerdeConfig.REGISTRY_URL, REGISTRY_URL);
        }
        /*
        vs = ConfigProvider.getConfig().getOptionalValue("apicurio.registry.avro-datum-provider",String.class);
        if (vs.isPresent()) {
            // Use Java reflection as the Avro Datum Provider - this also generates an Avro schema from the java bean
            properties.putIfAbsent(AbstractKafkaSerializer.AVRO_DATUM_PROVIDER, vs.get());
        }
        */

        vs = ConfigProvider.getConfig().getOptionalValue("app.apicurio.root.schema.artifactId", String.class);
        if (vs.isPresent()) {
            artifactId= vs.get();
        }
        vs = ConfigProvider.getConfig().getOptionalValue(SerdeConfig.EXPLICIT_ARTIFACT_GROUP_ID, String.class);
        if (vs.isPresent()) {
            groupId= vs.get();
            properties.put(SerdeConfig.EXPLICIT_ARTIFACT_GROUP_ID, groupId);
        }
       
        
        properties.put(SerdeConfig.ARTIFACT_RESOLVER_STRATEGY, RecordIdStrategy.class.getName());
        
        properties.forEach((k, val) -> logger.info(k + " : " + val));
        return properties;
    }

}
