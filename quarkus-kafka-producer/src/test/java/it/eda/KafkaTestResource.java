package it.eda;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startables;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

/**
 * Define a kafka broker for testing. 
 * Injected with @QuarkusTestResource to any test class.
 * This resource is started before the first test is run, 
 * and is closed at the end of the test suite.
 */
public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

    private static Network network = Network.newNetwork();
    public static final StrimziContainer kafkaContainer = new StrimziContainer()
    .withNetwork(network)
    .withNetworkAliases("kafka")
    .withExposedPorts(9092);

    @Override
    public Map<String, String> start() {
        
        Startables.deepStart(Stream.of(
        kafkaContainer)).join();  
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", kafkaContainer.getBootstrapServers());
        System.setProperty("QUARKUS_KAFKA_STREAMS_BOOTSTRAP_SERVERS", kafkaContainer.getBootstrapServers());
        HashMap<String,String> m = new HashMap<>();
        m.put("kafka.bootstrap.servers", kafkaContainer.getBootstrapServers());
        m.put("quarkus.kafka-streams.bootstrap-servers", kafkaContainer.getBootstrapServers());
        String[] topicNames = new String[]{"orders"};
        kafkaContainer.createTopics(topicNames);
        return Collections.synchronizedMap(m);
    }

    @Override
    public void stop() {
        kafkaContainer.close();
    }
    
}