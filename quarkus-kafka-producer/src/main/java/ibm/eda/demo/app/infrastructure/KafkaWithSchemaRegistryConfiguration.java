package ibm.eda.demo.app.infrastructure;

import java.util.Optional;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.eclipse.microprofile.config.ConfigProvider;

import io.apicurio.registry.utils.serde.AbstractKafkaSerDe;
import io.apicurio.registry.utils.serde.AbstractKafkaSerializer;
import io.apicurio.registry.utils.serde.strategy.FindLatestIdStrategy;
import io.apicurio.registry.utils.serde.strategy.RecordIdStrategy;

/**
 * Centralize in one class the Kafka Configuration when using schema registry. Useful when app has producer
 * and consumer
 */
public class KafkaWithSchemaRegistryConfiguration  extends KafkaConfiguration {
   
    public String REGISTRY_URL="http://localhost:8090/api";

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
            properties.putIfAbsent(AbstractKafkaSerDe.REGISTRY_URL_CONFIG_PARAM, vs.get());
            REGISTRY_URL=vs.get();
            if (! truststoreLocation.isEmpty()) {
                properties.put("value.converter.schema.registry.ssl.trutstore", truststoreLocation);
                properties.put("value.converter.schema.registry.ssl.trutstore.password",truststorePassword);
                properties.put("schema.registry.ssl.truststore.location",truststoreLocation);
                properties.put("schema.registry.ssl.truststore.password",truststorePassword);
            }
        } else {
            properties.putIfAbsent(AbstractKafkaSerDe.REGISTRY_URL_CONFIG_PARAM, REGISTRY_URL);
        }
        /*
        vs = ConfigProvider.getConfig().getOptionalValue("apicurio.registry.avro-datum-provider",String.class);
        if (vs.isPresent()) {
            // Use Java reflection as the Avro Datum Provider - this also generates an Avro schema from the java bean
            properties.putIfAbsent(AbstractKafkaSerializer.AVRO_DATUM_PROVIDER, vs.get());
        }
        */
       
        vs = ConfigProvider.getConfig().getOptionalValue("apicurio.registry.global-id",String.class);
        if (vs.isPresent()) {
            // Use Java reflection as the Avro Datum Provider - this also generates an Avro schema from the java bean
            properties.putIfAbsent(AbstractKafkaSerializer.REGISTRY_GLOBAL_ID_STRATEGY_CONFIG_PARAM,  vs.get());
        } else {
            properties.put(AbstractKafkaSerializer.REGISTRY_GLOBAL_ID_STRATEGY_CONFIG_PARAM, FindLatestIdStrategy.class.getName());
        }

        vs = ConfigProvider.getConfig().getOptionalValue("apicurio.registry.artifact-id",String.class);
        if (vs.isPresent()) {
            // Use Java reflection as the Avro Datum Provider - this also generates an Avro schema from the java bean
            properties.putIfAbsent(AbstractKafkaSerializer.REGISTRY_ARTIFACT_ID_STRATEGY_CONFIG_PARAM,  vs.get());
        } else {
            properties.put(AbstractKafkaSerializer.REGISTRY_ARTIFACT_ID_STRATEGY_CONFIG_PARAM, RecordIdStrategy.class.getName());
        }
        properties.forEach((k, val) -> logger.info(k + " : " + val));
        return properties;
    }

}
