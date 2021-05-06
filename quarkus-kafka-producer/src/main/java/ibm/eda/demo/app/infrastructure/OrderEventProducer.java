package ibm.eda.demo.app.infrastructure;

import java.util.List;
import java.util.UUID;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jboss.logging.Logger;

import ibm.eda.demo.app.infrastructure.events.EventEmitter;
import ibm.eda.demo.app.infrastructure.events.OrderEvent;

/**
 * Standard Kafka Producer without Avro and Schema Registry. 
 * If you want to use this code change the Inject and Named annotation in the OrderService component to use "default"
 */
@Singleton
@Named("default")
public class OrderEventProducer implements EventEmitter {
    Logger logger = Logger.getLogger(OrderEventProducer.class.getName());

    private KafkaProducer<String,OrderEvent> kafkaProducer = null;
    private KafkaConfiguration configuration = null;

    public OrderEventProducer() {
        super();
        configuration = new KafkaConfiguration();
        kafkaProducer = new KafkaProducer<String, OrderEvent>(configuration.getProducerProperties("OrderProducer_" + UUID.randomUUID()));
    }

    public OrderEventProducer(KafkaConfiguration configuration) {
        this.configuration = configuration;
    }

    public void sendOrderEvents(List<OrderEvent> l) {
        for (OrderEvent t : l) {
            emit(t);
        }
    }

    public void emit(OrderEvent oevent) { 
        ProducerRecord<String, OrderEvent> producerRecord = new ProducerRecord<String, OrderEvent>(
                configuration.getTopicName(), oevent.getOrderID(), oevent);
       
        logger.info("sending to " + configuration.getTopicName() + " item " + producerRecord
        .toString());
        try {
            this.kafkaProducer.send(producerRecord, new Callback() {

                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                    } else {
                        logger.info("The offset of the record just sent is: " + metadata.offset());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // logger.info("Partition:" + resp.partition());
    }

    
    @Override
    public void safeClose(){
        kafkaProducer.close();
        kafkaProducer = null;
    }
}
