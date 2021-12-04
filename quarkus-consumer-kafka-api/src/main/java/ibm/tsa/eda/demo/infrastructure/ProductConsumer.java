package ibm.tsa.eda.demo.infrastructure;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.mirror.RemoteClusterUtils;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import ibm.tsa.eda.demo.domain.Product;

@ApplicationScoped
public class ProductConsumer implements Runnable {
    private static final Logger logger = Logger.getLogger(ProductConsumer.class.getName());
    
    private ProductRepository repository;

    KafkaConfiguration configuration;
    private KafkaConsumer<String, String> kafkaConsumer = null;
    private boolean running = true;
    private Jsonb parser;

    public ProductConsumer( ProductRepository repository) {
        this.configuration = new KafkaConfiguration();
        this.repository = repository;
        init();
    }

    @Override
    public void run() {
        loop();
    }

    private void init() {
        parser = JsonbBuilder.create();
        kafkaConsumer = new KafkaConsumer<>(configuration.getConsumerProperties());
        kafkaConsumer.subscribe(Collections.singletonList(configuration.mainTopicName),
                new ConsumerRebalanceListener() {
                    @Override
                    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                        // do something
                    }

                    @Override
                    public void onPartitionsAssigned(Collection<org.apache.kafka.common.TopicPartition> partitions) {
                        try {
                            logger.log(Level.INFO, "Partitions " + partitions + " assigned, consumer seeking to end.");

                            for (TopicPartition partition : partitions) {
                                long position = kafkaConsumer.position(partition);
                                logger.log(Level.INFO, "current Position: " + position);

                                logger.log(Level.INFO, "Seeking to end...");
                                kafkaConsumer.seekToEnd(Arrays.asList(partition));
                                logger.log(Level.INFO,
                                        "Seek from the current position: " + kafkaConsumer.position(partition));
                                kafkaConsumer.seek(partition, position);
                            }
                            logger.log(Level.INFO, "Producer can now begin producing messages.");
                        } catch (final Exception e) {
                            logger.log(Level.INFO, "Error when assigning partitions" + e.getMessage());
                        }
                    }
                });
        getOffsetMapping();
    }

    private void loop() {
        int count = -1;
        // kafkaConsumer.seek();
        while (count <= configuration.pollRecords && running) {
            try {
                ConsumerRecords<String, String> records = kafkaConsumer
                        .poll(Duration.ofMillis(configuration.pollTimeout));
                for (ConsumerRecord<String, String> record : records) {
                    logger.log(Level.INFO,
                            "Consumer Record - key: " + record.key() + " timestamp: " + record.timestamp() + " value: "
                                    + record.value() + " partition: " + record.partition() + " offset: "
                                    + record.offset() + "\n");
                    Product p = parser.fromJson(record.value(), Product.class);
                    repository.addProduct(p);
                    if (configuration.pollRecords != -1)
                        count++;
                }
                if (!records.isEmpty() && configuration.commit) {
                    logger.log(Level.INFO, "Consumer manual commit");
                    kafkaConsumer.commitSync();
                }
            } catch (final Exception e) {
                logger.log(Level.ERROR, "Consumer loop has been unexpectedly interrupted " + e.getMessage());
                e.printStackTrace();
                // stop();
            }
        }
    }

    public void stop() {
        logger.log(Level.INFO, "Stop consumer");
        running = false;
    }

    private OffsetAndMetadata getOffsetMapping() {
        HashMap<String, Object> clientProperties = new HashMap();
        Properties sourceProperties = configuration.getConsumerProperties();
        for (String propName : sourceProperties.stringPropertyNames()) {
            clientProperties.put(propName, sourceProperties.getProperty(propName));
        }

        try {
            Map<TopicPartition, OffsetAndMetadata> newOffsets = RemoteClusterUtils.translateOffsets(clientProperties,
                    configuration.groupId, configuration.sourceClusterAlias,
                    Duration.ofMillis(configuration.pollTimeout));
                    newOffsets.forEach( (k,v) -> logger.info(k + " -> " + v));
                    return newOffsets.get(new TopicPartition(configuration.mainTopicName,0));
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (TimeoutException e) {
            e.printStackTrace();
        } 
        return null;
    }
}
