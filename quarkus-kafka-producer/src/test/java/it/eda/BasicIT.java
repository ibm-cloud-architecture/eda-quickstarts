package it.eda;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startables;


public abstract class BasicIT {
    
    private static Network network = Network.newNetwork();
    
    public static StrimziContainer kafkaContainer = new StrimziContainer()
            .withNetwork(network)
            .withNetworkAliases("kafka")
            .withExposedPorts(9092);

    @BeforeAll
    public static void startAll() {
        Startables.deepStart(Stream.of(
        kafkaContainer)).join();
    }

    @AfterAll
    public static void stopAll() {
        kafkaContainer.stop();
       
    }
}
