package it.eda;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ContainerNetwork;

import org.jboss.logging.Logger;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.images.builder.Transferable;

/**
 * Simple test container for running strimzi inside Test container. simple version
 * of https://github.com/strimzi/strimzi-kafka-operator/blob/main/test-container/src/main/java/io/strimzi/StrimziKafkaContainer.java
 */
public class StrimziContainer extends GenericContainer<StrimziContainer>{
    private static Logger LOG = Logger.getLogger(StrimziContainer.class.getName());
    private static final String STARTER_SCRIPT = "/testcontainers_start.sh";
    public static String VERSION = "latest-kafka-2.7.0";
    private static final int KAFKA_PORT = 9092;
    private static final int ZOOKEEPER_PORT = 2181;
    private int kafkaExposedPort;
    
    public StrimziContainer(String version){
        super("quay.io/strimzi/kafka:" + version);
        super.withNetwork(Network.SHARED);
        super.withExposedPorts(KAFKA_PORT);

        // exposing kafka port from the container
        withExposedPorts(KAFKA_PORT);

        withEnv("LOG_DIR", "/tmp");
    }

    public StrimziContainer() {
        this(VERSION);
    }

    public String getBootstrapServers() {
        return String.format("%s:%s", getContainerIpAddress(), kafkaExposedPort);
    }

    @Override
    protected void doStart() {
        // we need it for the startZookeeper(); and startKafka(); to run before...
        withCommand("sh", "-c", "while [ ! -f " + STARTER_SCRIPT + " ]; do sleep 0.1; done; " + STARTER_SCRIPT);
        super.doStart();
    }

    @Override
    /**
     * Build the list of kafka listeners
     * Build the starter script to ensure zookeeper and kafka are started in this order
     */
    protected void containerIsStarting(InspectContainerResponse containerInfo, boolean reused) {
        super.containerIsStarting(containerInfo, reused);

        kafkaExposedPort = getMappedPort(KAFKA_PORT);

        LOG.info("This is mapped port " + kafkaExposedPort);

        StringBuilder advertisedListeners = new StringBuilder("PLAINTEXT://" + getBootstrapServers());

        Collection<ContainerNetwork> cns = containerInfo.getNetworkSettings().getNetworks().values();

        int advertisedListenerNumber = 1;
        List<String> advertisedListenersNames = new ArrayList<>();

        for (ContainerNetwork cn : cns) {
            // must be always unique
            final String advertisedName = "BROKER" + advertisedListenerNumber;
            advertisedListeners.append(",").append(advertisedName).append("://").append(cn.getIpAddress()).append(":9093");
            advertisedListenersNames.add(advertisedName);
            advertisedListenerNumber++;
        }

        LOG.info("This is all advertised listeners for Kafka " + advertisedListeners.toString());

        StringBuilder kafkaListeners = new StringBuilder();
        StringBuilder kafkaListenerSecurityProtocol = new StringBuilder();

        advertisedListenersNames.forEach(name -> {
            // listeners
            kafkaListeners.append(name);
            kafkaListeners.append("://0.0.0.0:9093");
            kafkaListeners.append(",");
            // listener.security.protocol.map
            kafkaListenerSecurityProtocol.append(name);
            kafkaListenerSecurityProtocol.append(":PLAINTEXT");
            kafkaListenerSecurityProtocol.append(",");
        });

        String command = "#!/bin/bash \n";
        command += "bin/zookeeper-server-start.sh config/zookeeper.properties &\n";
        command += "bin/kafka-server-start.sh config/server.properties --override listeners=" + kafkaListeners + "PLAINTEXT://0.0.0.0:" + KAFKA_PORT +
            " --override advertised.listeners=" + advertisedListeners +
            " --override zookeeper.connect=localhost:" + ZOOKEEPER_PORT +
            " --override listener.security.protocol.map=" + kafkaListenerSecurityProtocol + "PLAINTEXT:PLAINTEXT" +
            " --override inter.broker.listener.name=BROKER1\n";

        LOG.info("Copying command to 'STARTER_SCRIPT' script.");

        copyFileToContainer(
            Transferable.of(command.getBytes(StandardCharsets.UTF_8), 700),
            STARTER_SCRIPT
        );
    }
}
