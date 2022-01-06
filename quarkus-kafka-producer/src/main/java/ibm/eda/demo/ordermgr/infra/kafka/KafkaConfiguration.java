package ibm.eda.demo.ordermgr.infra.kafka;

import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.apicurio.registry.utils.serde.AbstractKafkaSerDe;
import io.apicurio.registry.utils.serde.AbstractKafkaSerializer;
import io.apicurio.registry.utils.serde.AbstractKafkaStrategyAwareSerDe;
import io.apicurio.registry.utils.serde.strategy.FindLatestIdStrategy;
import io.apicurio.registry.utils.serde.strategy.RecordIdStrategy;

/**
 * Centralize in one class the Kafka Configuration. Useful when app has producer
 * and consumer
 */
@ApplicationScoped
public class KafkaConfiguration {
    protected static final Logger logger = Logger.getLogger(KafkaConfiguration.class.getName());

    @ConfigProperty(name="app.producer.prefix.clientid",defaultValue="order-ms")
    protected String clientID;
  

    @ConfigProperty(name="kafka.topic.name", defaultValue="orders")
    protected  String mainTopicName;
    @ConfigProperty(name="kafka.bootstrap.servers",defaultValue="localhost:9092")
    protected  String bootstrapServers; 

    @ConfigProperty(name="kafka.sasl.mechanism")
    protected Optional<String> saslMechanism;
    @ConfigProperty(name = "kafka.sasl.jaas.config")
    public Optional<String> saslJaasConfig;
    @ConfigProperty(name="kafka.security.protocol",defaultValue="PLAINTEXT")
    protected  String securityProtocol;
    @ConfigProperty(name="kafka.ssl.truststore.location")
    protected  Optional<String> truststoreLocation;
    @ConfigProperty(name="kafka.ssl.truststore.password")
    protected  Optional<String> truststorePassword;
    @ConfigProperty(name="kafka.ssl.truststore.type")
    protected Optional<String> truststoreType;
    @ConfigProperty(name="kafka.ssl.keystore.location")
    protected  Optional<String> keystoreLocation;
    @ConfigProperty(name="kafka.ssl.keystore.password")
    protected  Optional<String> keystorePassword;
    @ConfigProperty(name="kafka.ssl.keystore.type")
    protected  Optional<String> keystoreType;
   
    @ConfigProperty(name="apicurio.registry.url")
    protected String REGISTRY_URL;
    @ConfigProperty(name="app.producer.schema.groupId",defaultValue="OrderGroup")
    protected String groupId;
    @ConfigProperty(name="app.producer.schema.artifactId",defaultValue="OrderEvent")
    protected String artifactId;

    // Producer specifics
    @ConfigProperty(name = "kafka.producer.timeout.sec")
    public  Optional<Integer> producerTimeout;
    @ConfigProperty(name = "kafka.producer.acks", defaultValue = "all")
    public Optional<String> producerAck;
    @ConfigProperty(name = "kafka.producer.idempotence")
    public Optional<Boolean> enableIdempotence;
    @ConfigProperty(name = "kafka.producer.retries")
    public Optional<Integer> producerRetries;

    @ConfigProperty(name = "kafka.key.serializer", defaultValue = "org.apache.kafka.common.serialization.StringSerializer")
    public Optional<String> keySerializer;
    @ConfigProperty(name = "kafka.value.serializer", defaultValue = "ibm.eda.demo.ordermgr.infra.events.OrderEventSerializer")
    public Optional<String> valueSerializer;



    public KafkaConfiguration(){}

    public KafkaConfiguration(String bootStrap) {
        this.bootstrapServers = bootStrap;
    }

    public KafkaConfiguration(String bootstrapServers, String schemaRegistryURL) {
        this.bootstrapServers = bootstrapServers;
        this.REGISTRY_URL = schemaRegistryURL;
    }

    /**
     * @return common kafka properties
     */
    private  Properties buildCommonProperties() {
        Properties properties = new Properties();
        if (getBootstrapServers() == null) {
            bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
            if (bootstrapServers == null) {
                bootstrapServers = ConfigProvider.getConfig().getValue("kafka.bootstrap.servers", String.class);
        
            }
        }
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, getBootstrapServers());
        
        if (getSecurityProtocol() != null) {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getSecurityProtocol());
        }
        if (getSaslMechanism().isPresent()) {
            properties.put(SaslConfigs.SASL_MECHANISM, getSaslMechanism().get());
        }
      
        if (getSaslJaasConfig().isPresent()) {
            properties.put(SaslConfigs.SASL_JAAS_CONFIG, getSaslJaasConfig().get());
        }

        properties.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
        properties.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
        properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "HTTPS");
        
        if (getSecurityProtocol().contains("SSL")) {
            if (getTruststoreLocation() != null) {
                properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,getTruststoreLocation());
                properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG,getTruststorePassword());
                properties.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG,getTruststoreType());
            }
        }
        if (getSecurityProtocol().equals("SSL")) {
            properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,getKeystoreLocation());
            properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG,getKeystorePassword()); 
            properties.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG,getKeystoreType());   
        }

        
        if (keySerializer.isPresent()) {
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer.get());
        } else {
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        }

        if (valueSerializer.isPresent()) {
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer.get());
        } else {
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        }
     
        if (getRegistryURL() != null) {
            properties.putIfAbsent(AbstractKafkaSerDe.REGISTRY_URL_CONFIG_PARAM, getRegistryURL());
            properties.put(AbstractKafkaSerDe.USE_HEADERS,"true");
            if (! getTruststoreLocation().isEmpty()) {
                // properties.put("value.converter.schema.registry.ssl.trutstore", truststoreLocation);
                // properties.put("value.converter.schema.registry.ssl.trutstore.password",truststorePassword);
                // properties.put("schema.registry.ssl.truststore.location",truststoreLocation);
                // properties.put("schema.registry.ssl.truststore.password",truststorePassword);
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_TRUSTSTORE_LOCATION, getTruststoreLocation());
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_TRUSTSTORE_PASSWORD, getTruststorePassword());
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_TRUSTSTORE_TYPE, getTruststoreType());

            }
            if (! getKeystoreLocation().isEmpty()) {
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_KEYSTORE_LOCATION,getKeystoreLocation());
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_KEYSTORE_PASSWORD,getKeystorePassword());
                properties.put(AbstractKafkaStrategyAwareSerDe.REGISTRY_REQUEST_KEYSTORE_TYPE, getKeystoreType());
            }
        } 
        /*
        vs = ConfigProvider.getConfig().getOptionalValue("apicurio.registry.avro-datum-provider",String.class);
        if (vs.isPresent()) {
            // Use Java reflection as the Avro Datum Provider - this also generates an Avro schema from the java bean
            properties.putIfAbsent(AbstractKafkaSerializer.AVRO_DATUM_PROVIDER, vs.get());
        }
        */
       
        properties.putIfAbsent(AbstractKafkaSerializer.REGISTRY_ARTIFACT_ID_STRATEGY_CONFIG_PARAM,
            RecordIdStrategy.class.getName());
        properties.putIfAbsent(AbstractKafkaSerializer.REGISTRY_GLOBAL_ID_STRATEGY_CONFIG_PARAM,
        FindLatestIdStrategy.class.getName());
       
        // The default approach is to pass the global ID in the message payload. If you want the ID sent in the message headers instead:
        //properties.putIfAbsent(AbstractKafkaSerDe.USE_HEADERS, "true");
        return properties;
    }



   
    public  Properties getProducerProperties() {
        Properties properties = buildCommonProperties();
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientID + "-" + UUID.randomUUID());
     

        if ( getProducerTimeout().isPresent()) {
            properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, getProducerTimeout().get());
        }

        if ( getProducerAck().isPresent()) {
            properties.put(ProducerConfig.ACKS_CONFIG, getProducerAck().get());
        } else {
            properties.put(ProducerConfig.ACKS_CONFIG, "all");
        }
    
        if (getEnableIdempotence().isPresent()) {
            properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, getEnableIdempotence().get());
            properties.put(ProducerConfig.RETRIES_CONFIG, 1);
        } else {
            properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
            if (getProducerRetries().isPresent()) {
                properties.put(ProducerConfig.RETRIES_CONFIG, getProducerRetries().get());
            } else {
                properties.put(ProducerConfig.RETRIES_CONFIG, 0);
            }
        }
    
        // other way to access parameter
        // Optional<Integer> vi = ConfigProvider.getConfig().getOptionalValue("kafka.producer.retries", Integer.class);
   
        properties.forEach((k, val) -> logger.info(k + " : " + val));
        return properties;
    }

    public  String getTopicName(){
        return mainTopicName;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getRegistryURL() {
        return REGISTRY_URL;
    }

    public String getClientID() {
        return clientID;
    }

    public String getMainTopicName() {
        return mainTopicName;
    }

    public Optional<String> getSaslMechanism() {
        return saslMechanism;
    }

    public String getSecurityProtocol() {
        return securityProtocol;
    }

    public Optional<String> getTruststoreLocation() {
        
        return truststoreLocation;
    }

    public String getTruststorePassword() {
        return truststorePassword.get();
    }

    public String getTruststoreType(){
        return truststoreType.get();
    }

    public Optional<String> getKeystoreLocation() {
        return keystoreLocation;
    }

    
    public String getKeystorePassword() {
        return keystorePassword.get();
    }
    
    public String getKeystoreType(){
        return keystoreType.get();
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public Optional<Integer> getProducerTimeout() {
        return producerTimeout;
    }

    public Optional<String> getProducerAck() {
        return producerAck;
    }

    public Optional<String> getSaslJaasConfig() {
        return saslJaasConfig;
    }

    

    public Optional<Boolean> getEnableIdempotence(){
        return enableIdempotence;
    }

    public Optional<Integer> getProducerRetries() {
        return producerRetries;
    }
    
}
