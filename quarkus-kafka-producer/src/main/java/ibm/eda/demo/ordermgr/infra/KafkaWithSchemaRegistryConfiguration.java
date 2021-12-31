package ibm.eda.demo.ordermgr.infra;

import java.util.Optional;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.apicurio.registry.utils.serde.AbstractKafkaSerDe;
import io.apicurio.registry.utils.serde.AbstractKafkaSerializer;
import io.apicurio.registry.utils.serde.AbstractKafkaStrategyAwareSerDe;
import io.apicurio.registry.utils.serde.strategy.FindBySchemaIdStrategy;
import io.apicurio.registry.utils.serde.strategy.SimpleTopicIdStrategy;

//import io.apicurio.registry.serde.SerdeConfig;
//import io.apicurio.registry.serde.avro.strategy.RecordIdStrategy;
//import io.apicurio.registry.utils.serde.AbstractKafkaStrategyAwareSerDe;
/**
 * Centralize in one class the Kafka Configuration when using schema registry. Useful when app has producer
 * and consumer
 */
@ApplicationScoped
public class KafkaWithSchemaRegistryConfiguration  extends KafkaConfiguration {
   
    public String REGISTRY_URL="http://localhost:8091/";
    @Inject
    @ConfigProperty(name="app.producer.schema.file.path",defaultValue="src/main/avro/OrderEvnt.avsc")
    private String schemaPath;
    @Inject
    @ConfigProperty(name="app.producer.schema.groupId",defaultValue="OrderGroup")
    private String groupId = "OrderGroup";
    

    @Inject
    @ConfigProperty(name="app.producer.schema.artifactId",defaultValue="OrderEvent")
    private String artifactId;

    public  Properties getProducerPropertiesWithSchemaRegistry() {
        Properties properties = getProducerProperties();
        
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
            REGISTRY_URL=vs.get();
            properties.putIfAbsent(AbstractKafkaSerDe.REGISTRY_URL_CONFIG_PARAM, REGISTRY_URL);
                
            if (! truststoreLocation.isEmpty()) {
                // properties.put("value.converter.schema.registry.ssl.trutstore", truststoreLocation);
                // properties.put("value.converter.schema.registry.ssl.trutstore.password",truststorePassword);
                // properties.put("schema.registry.ssl.truststore.location",truststoreLocation);
                // properties.put("schema.registry.ssl.truststore.password",truststorePassword);
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_TRUSTSTORE_LOCATION, truststoreLocation);
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_TRUSTSTORE_PASSWORD, truststorePassword);
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_TRUSTSTORE_TYPE, "PKCS12");

            }
            if (! keystoreLocation.isEmpty()) {
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_KEYSTORE_LOCATION,keystoreLocation);
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_KEYSTORE_PASSWORD,keystorePassword);
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_KEYSTORE_TYPE, "PKCS12");
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

        vs = ConfigProvider.getConfig().getOptionalValue("app.apicurio.root.schema.artifactId", String.class);
        if (vs.isPresent()) {
            artifactId= vs.get();
        }
       
        properties.putIfAbsent(AbstractKafkaSerializer.REGISTRY_ARTIFACT_ID_STRATEGY_CONFIG_PARAM,
            SimpleTopicIdStrategy.class.getName());
        properties.putIfAbsent(AbstractKafkaSerializer.REGISTRY_GLOBAL_ID_STRATEGY_CONFIG_PARAM,
            FindBySchemaIdStrategy.class.getName());
        properties.forEach((k, val) -> logger.info(k + " : " + val));
        return properties;
    }

    public String getSchemaPath(){
        return schemaPath;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }
}
