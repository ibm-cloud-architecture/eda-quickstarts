package ibm.eda.demo.app.infrastructure;

import java.util.List;
import java.util.UUID;

import javax.inject.Singleton;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jboss.logging.Logger;

import ibm.eda.demo.app.infrastructure.events.EventEmitter;
import ibm.eda.demo.app.infrastructure.events.OrderEventOld;

@Singleton
public class OrderEventAvroProducer implements EventEmitter {
    Logger logger = Logger.getLogger(OrderEventProducer.class.getName());

    private KafkaProducer<String,OrderEventOld> kafkaProducer = null;
    private KafkaAvroConfiguration configuration = null;

    public OrderEventAvroProducer() {
        super();
        configuration = new KafkaAvroConfiguration();
        kafkaProducer = new KafkaProducer<String, OrderEventOld>(configuration.getProducerProperties("OrderProducer_" + UUID.randomUUID()));
    }

    public void sendOrderEvents(List<OrderEventOld> l) {
        for (OrderEventOld t : l) {
            emit(t);
        }
    }

    public void emit(OrderEventOld oevent) { 
        ProducerRecord<String, OrderEventOld> producerRecord = new ProducerRecord<String, OrderEventOld>(
                configuration.getTopicName(), oevent.getPayload().getOrderID(), oevent);
       
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
        } finally {
            safeClose();
        }
        // logger.info("Partition:" + resp.partition());
    }

    
    @Override
    public void safeClose(){
        kafkaProducer.flush();
        kafkaProducer.close();
        kafkaProducer = null;
    }
}
