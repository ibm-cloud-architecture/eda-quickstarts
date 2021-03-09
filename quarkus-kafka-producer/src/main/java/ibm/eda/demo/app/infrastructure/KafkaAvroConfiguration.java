package ibm.eda.demo.app.infrastructure;

import java.util.Optional;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Centralize in one class the Kafka Configuration. Useful when app has producer
 * and consumer
 */
public class KafkaAvroConfiguration  extends KafkaConfiguration {
    // event stream specific
    public  String schemaName = "Order";
    public  String schemaVersion = "1.0.0";
 

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
            // event streams setting
            // properties.putIfAbsent(SchemaRegistryConfig.PROPERTY_API_URL, vs.get());  
            // properties.putIfAbsent(SchemaRegistryConfig.PROPERTY_API_SKIP_SSL_VALIDATION, true);
            //properties.putIfAbsent("schema.registry.url",vs.get());
            // properties.putIfAbsent("value.converter.schema.registry.url",vs.get());
            // Apicurio settings
            properties.putIfAbsent(AbstractKafkaSerDe.REGISTRY_URL_CONFIG_PARAM, vs.get());
            properties.put(AbstractKafkaSerializer.REGISTRY_GLOBAL_ID_STRATEGY_CONFIG_PARAM, FindLatestIdStrategy.class.getName());
            if (! truststoreLocation.isEmpty()) {
                properties.put("value.converter.schema.registry.ssl.trutstore", truststoreLocation);
                properties.put("value.converter.schema.registry.ssl.trutstore.password",truststorePassword);
                properties.put("schema.registry.ssl.truststore.location",truststoreLocation);
                properties.put("schema.registry.ssl.truststore.password",truststorePassword);
            }
           
           
           // properties.put(SchemaRegistryConfig.PROPERTY_ENCODING_TYPE, SchemaRegistryConfig.ENCODING_BINARY);
        } 
        properties.putIfAbsent(SerdeConfig.AUTO_REGISTER_ARTIFACT, Boolean.TRUE);
        // Use Java reflection as the Avro Datum Provider - this also generates an Avro schema from the java bean
        properties.putIfAbsent(AvroKafkaSerdeConfig.AVRO_DATUM_PROVIDER, ReflectAvroDatumProvider.class.getName());
        return properties;
    }

    private Properties eventStreamsSpecifics( Properties properties){
        Optional<String> vs = ConfigProvider.getConfig().getOptionalValue("schema.name", String.class);
        if (vs.isPresent()) {
            schemaName=vs.get();
        } 
        vs = ConfigProvider.getConfig().getOptionalValue("schema.version", String.class);
        if (vs.isPresent()) {
            schemaVersion=vs.get();
        } 
        return properties;
    }
}
