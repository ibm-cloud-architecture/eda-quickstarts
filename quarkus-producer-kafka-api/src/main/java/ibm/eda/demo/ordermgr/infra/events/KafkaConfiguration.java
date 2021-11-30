package ibm.eda.demo.ordermgr.infra.events;

import java.util.Optional;
import java.util.Properties;

import javax.inject.Singleton;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Centralize in one class the Kafka Configuration. Useful when app has producer
 * and consumer
 */
@Singleton
public class KafkaConfiguration {
    protected static final Logger logger = Logger.getLogger(KafkaConfiguration.class.getName());

    @ConfigProperty(name = "kafka.topic.name")
    public  String mainTopicName = "orders";
    @ConfigProperty(name = "kafka.bootstrap.servers")
    public  String bootstrapServers = "localhost:9092"; 
    @ConfigProperty(name = "kafka.security.protocol")
    public String securityProtocol;
    @ConfigProperty(name = "kafka.sasl.mechanism")
    public String saslMechanism;
    @ConfigProperty(name = "kafka.sasl.jaas.config")
    public String saslJaasConfig;
    @ConfigProperty(name = "kafka.ssl.truststore.location")
    public  String truststoreLocation;
    @ConfigProperty(name = "kafka.ssl.truststore.password")
    public  String truststorePassword = "";
    @ConfigProperty(name = "kafka.ssl.keystore.location")
    public  String keystoreLocation;
    @ConfigProperty(name = "kafka.ssl.keystore.password")
    public  String keystorePassword;
    // Producer specifics
    @ConfigProperty(name = "kafka.producer.timeout.sec")
    public  Long producerTimeout;
    @ConfigProperty(name = "kafka.producer.acks")
    public String producerAck;
    @ConfigProperty(name = "kafka.producer.clientID")
    public String clientID = "qs-prod-01";
    @ConfigProperty(name = "kafka.producer.idempotence")
    public Boolean enableIdempotence= false;
    @ConfigProperty(name = "kafka.producer.retries")
    public Integer producerRetries = 0;
    @ConfigProperty(name = "kafka.key.serializer")
    public String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    @ConfigProperty(name = "kafka.value.serializer")
    public String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";
    public  String schemaName = "Order";
    public  String schemaVersion = "1.0.0";
    public  String schemaRegistryURL = null;

    public KafkaConfiguration(){}

    public KafkaConfiguration(String bootStrap) {
        this.bootstrapServers = bootStrap;
    }

    public KafkaConfiguration(String bootstrapServers, String schemaRegistryURL) {
        this.bootstrapServers = bootstrapServers;
        this.schemaRegistryURL = schemaRegistryURL;
    }

    /**
     * @return common kafka properties
     */
    private  Properties buildCommonProperties() {
        Properties properties = new Properties();

        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        if (securityProtocol != null) {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        }
        if (saslMechanism != null) {
            properties.put(SaslConfigs.SASL_MECHANISM,saslMechanism );
        }
      
        if (saslJaasConfig != null) {
            properties.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }
        properties.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
        properties.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
        properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "HTTPS");
        
        if (truststoreLocation != null) {
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, truststoreLocation);
        }

        if (truststorePassword != null) {
            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, truststorePassword);
        }

        if (keystoreLocation != null) {
            properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keystoreLocation);
        }

        if (keystorePassword != null) {
            properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keystorePassword);
        }
      
        return properties;
    }



   
    public  Properties getProducerProperties() {
        Properties properties = buildCommonProperties();
        
        if (producerTimeout != null) {
            properties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, producerTimeout);
        }

        if (producerAck != null) {
            properties.put(ProducerConfig.ACKS_CONFIG, producerAck);
        } else {
            properties.put(ProducerConfig.ACKS_CONFIG, "all");
        }
    
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, enableIdempotence);
      
        //Optional<Integer> vi = ConfigProvider.getConfig().getOptionalValue("kafka.producer.retries", Integer.class);
        properties.put(ProducerConfig.RETRIES_CONFIG, producerRetries);
       
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);

        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientID);
       
        properties.forEach((k, val) -> logger.info(k + " : " + val));
        return properties;
    }

    public  String getTopicName(){
        return mainTopicName;
    }
}
