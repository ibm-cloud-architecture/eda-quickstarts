package ibm.tsa.eda.demo.domain.infrastructure;

import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import javax.inject.Singleton;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

/**
 * Centralize in one class the Kafka Configuration. Useful when app has producer
 * and consumer
 */
@Singleton
public class KafkaConfiguration {
    private static final Logger logger = Logger.getLogger(KafkaConfiguration.class.getName());

    public String mainTopicName = "products";
    public String groupId = "products-consumer-group";
    public boolean commit = false;
    public String offsetPolicy = "latest";
    public long pollTimeout = 1000;
    public long pollRecords = 10;
    public String targetClusterAlias;
    public String sourceClusterAlias;

    public KafkaConfiguration() {
        Optional<Long> v = ConfigProvider.getConfig().getOptionalValue("kafka.consumer.poll.records", Long.class);
        if (v.isPresent()) {
            pollRecords = v.get();
        }

        Optional<Long> timeout = ConfigProvider.getConfig().getOptionalValue("kafka.consumer.poll.timeout", Long.class);
        if (timeout.isPresent()) {
            pollTimeout = timeout.get();
        }

        Optional<String> group = ConfigProvider.getConfig().getOptionalValue("kafka.consumer.groupId", String.class);
        if (group.isPresent()) {
            groupId = group.get();
        }

        Optional<String> topic = ConfigProvider.getConfig().getOptionalValue("kafka.topic.name", String.class);
        if (topic.isPresent()) {
            mainTopicName = topic.get();
        }

        Optional<String> offsetPolicy = ConfigProvider.getConfig().getOptionalValue("kafka.consumer.offsetPolicy", String.class);
        if (offsetPolicy.isPresent()) {
            this.offsetPolicy = offsetPolicy.get();
        }

        Optional<Boolean> commit = ConfigProvider.getConfig().getOptionalValue("kafka.consumer.commit", Boolean.class);
        if (commit.isPresent()) {
            this.commit = commit.get();
        }

        Optional<String> cluster = ConfigProvider.getConfig().getOptionalValue("mm2.target.cluster.alias", String.class);
        if (cluster.isPresent()) {
            targetClusterAlias = cluster.get();
        }

        cluster = ConfigProvider.getConfig().getOptionalValue("mm2.source.cluster.alias", String.class);
        if (cluster.isPresent()) {
            sourceClusterAlias = cluster.get();
        }
        
    }

    /**
     * @return common kafka properties
     */
    private Properties buildCommonProperties() {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
                ConfigProvider.getConfig().getValue("kafka.brokers", String.class));
        Optional<String> v = ConfigProvider.getConfig().getOptionalValue("kafka.security.protocol", String.class);
        if (v.isPresent()) {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, v.get());
        }
        v = ConfigProvider.getConfig().getOptionalValue("kafka.sasl.mechanism", String.class);
        if (v.isPresent()) {
            properties.put(SaslConfigs.SASL_MECHANISM, v.get());
        }
      
        v = ConfigProvider.getConfig().getOptionalValue("kafka.sasl.jaas.config", String.class);
        if (v.isPresent()) {
            properties.put(SaslConfigs.SASL_JAAS_CONFIG, v.get());
        }
        properties.put(SslConfigs.SSL_PROTOCOL_CONFIG, "TLSv1.2");
        properties.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1.2");
        properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "HTTPS");
        
        v = ConfigProvider.getConfig().getOptionalValue("kafka.ssl.trustore.path", String.class);
        if (v.isPresent()) {
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, v.get());
        }

        v = ConfigProvider.getConfig().getOptionalValue("kafka.ssl.trustore.password", String.class);
        if (v.isPresent()) {
            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, v.get());
        }

        return properties;
    }



    public Properties getConsumerProperties(String groupID, boolean commit, String offset) {
        Properties properties = buildCommonProperties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, commit);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetPolicy);
        
        Optional<String> v = ConfigProvider.getConfig().getOptionalValue("kafka.consumer.key.deserializer", String.class);
        if (v.isPresent()) {
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, v.get());
        } else {
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        }

        v = ConfigProvider.getConfig().getOptionalValue("kafka.consumer.value.deserializer", String.class);
        if (v.isPresent()) {
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, v.get());
        } else {
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        }
      
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, groupID + "-client-" + UUID.randomUUID());
        properties.forEach((key, value) -> logger.info(key + " : " + value));
        return properties;
    }

    public Properties getConsumerProperties() {
        return getConsumerProperties(this.groupId, this.commit, this.offsetPolicy);
    }

}
