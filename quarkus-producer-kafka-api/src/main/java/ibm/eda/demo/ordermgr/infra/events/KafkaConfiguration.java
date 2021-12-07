package ibm.eda.demo.ordermgr.infra.events;

import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

/**
 * Centralize in one class the Kafka Configuration. Useful when app has producer
 * and consumer
 */
@ApplicationScoped
public class KafkaConfiguration {
    protected static final Logger logger = Logger.getLogger(KafkaConfiguration.class.getName());

    @ConfigProperty(name = "kafka.topic.name", defaultValue="orders")
    public  String mainTopicName;
    @Inject
    @ConfigProperty(name = "kafka.bootstrap.servers")
    public  String bootstrapServers; 
    @ConfigProperty(name = "kafka.security.protocol")
    public String securityProtocol;
    @ConfigProperty(name = "kafka.sasl.mechanism")
    public Optional<String> saslMechanism;
    @ConfigProperty(name = "kafka.sasl.jaas.config")
    public Optional<String> saslJaasConfig;
    @ConfigProperty(name = "kafka.ssl.truststore.location")
    public  Optional<String> truststoreLocation;
    @ConfigProperty(name = "kafka.ssl.truststore.password")
    public  Optional<String> truststorePassword;
    @ConfigProperty(name = "kafka.ssl.keystore.location")
    public  Optional<String> keystoreLocation;
    @ConfigProperty(name = "kafka.ssl.keystore.password")
    public  Optional<String> keystorePassword;
    // Producer specifics
    @ConfigProperty(name = "kafka.producer.timeout.sec")
    public  Long producerTimeout;
    @ConfigProperty(name = "kafka.producer.acks", defaultValue = "all")
    public Optional<String> producerAck;
    @ConfigProperty(name = "kafka.producer.clientID")
    public String clientID = "qs-prod-01";
    @ConfigProperty(name = "kafka.producer.idempotence")
    public Boolean enableIdempotence= false;
    @ConfigProperty(name = "kafka.producer.retries")
    public Integer producerRetries = 0;
    @ConfigProperty(name = "kafka.key.serializer")
    public String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
    @ConfigProperty(name = "kafka.value.serializer")
    public String valueSerializer = "ibm.eda.demo.ordermgr.infra.events.OrderEventSerializer";
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

    void onStart(@Observes StartupEvent ev) {
        logger.info("The application is starting...");
        getTopicName();
    }

    void onStop(@Observes ShutdownEvent ev) {
        logger.info("The application is stopping...");
    }

    /**
     * @return common kafka properties
     */
    private  Properties buildCommonProperties() {
        Properties properties = new Properties();

        if (bootstrapServers == null) {
            bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
            if (bootstrapServers == null) {
                bootstrapServers = ConfigProvider.getConfig().getValue("kafka.bootstrap.servers", String.class);
        
            }
        }
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
        
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientID + UUID.randomUUID().toString());
       
        properties.forEach((k, val) -> logger.info(k + " : " + val));
        return properties;
    }

    public  String getTopicName(){
        return mainTopicName;
    }
}
