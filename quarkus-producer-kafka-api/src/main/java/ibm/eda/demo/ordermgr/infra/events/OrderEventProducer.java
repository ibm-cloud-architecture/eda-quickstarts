package ibm.eda.demo.ordermgr.infra.events;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jboss.logging.Logger;

@ApplicationScoped
public class OrderEventProducer {
    Logger logger = Logger.getLogger(OrderEventProducer.class.getName());

    private KafkaProducer<String,OrderEvent> kafkaProducer = null;
    private KafkaConfiguration configuration = null;

    public OrderEventProducer() {
        super();
        configuration = new KafkaConfiguration();
        kafkaProducer = new KafkaProducer<String, OrderEvent>(configuration.getProducerProperties());
    }

    public OrderEventProducer(KafkaConfiguration configuration) {
        this.configuration = configuration;
        kafkaProducer = new KafkaProducer<String, OrderEvent>(configuration.getProducerProperties());
    
    }

    public void sendOrderEvents(List<OrderEvent> l) {
        for (OrderEvent t : l) {
            emit(t);
        }
    }

    public void emit(OrderEvent oevent) { 
        ProducerRecord<String, OrderEvent> producerRecord = new ProducerRecord<String, OrderEvent>(
                configuration.getTopicName(), oevent.orderID, oevent);
        
        logger.info("sending to " + configuration.getTopicName() + " item: " + producerRecord
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

    public void safeClose(){
        kafkaProducer.close();
        kafkaProducer = null;
    }
}
