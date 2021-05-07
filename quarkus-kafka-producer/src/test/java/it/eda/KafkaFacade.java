package it.eda;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.testcontainers.containers.KafkaContainer;

public class KafkaFacade {
    private KafkaContainer kafkaContainer;
    private AdminClient client;

    private static KafkaFacade instance;

    public static KafkaFacade getInstance() {
        if (instance == null) {
            instance = new KafkaFacade();
        }
        return instance;
    }

    private KafkaFacade() {
        //hidden constructor, singleton class
    }

    public void createTopic(String topic, int partitions, int replicationFactor) {
        adminClient().createTopics(Arrays.asList(new NewTopic(topic, partitions, (short) replicationFactor)));
    }

    public String bootstrapServers() {
        if (kafkaContainer != null) {
            return kafkaContainer.getBootstrapServers();
        }
        return null;
    }

    private AdminClient adminClient() {
        if (client == null) {
            Properties properties = new Properties();
            properties.put("bootstrap.servers", bootstrapServers());
            properties.put("connections.max.idle.ms", 10000);
            properties.put("request.timeout.ms", 5000);
            client = AdminClient.create(properties);
        }
        return client;
    }
}
