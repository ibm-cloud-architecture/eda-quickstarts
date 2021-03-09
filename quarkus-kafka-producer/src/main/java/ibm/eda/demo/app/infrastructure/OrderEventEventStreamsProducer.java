package ibm.eda.demo.app.infrastructure;

import java.util.UUID;

import javax.inject.Singleton;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.jboss.logging.Logger;

import ibm.eda.demo.app.infrastructure.events.EventEmitter;
import ibm.eda.demo.app.infrastructure.events.OrderEventOld;

@Singleton
public class OrderEventEventStreamsProducer implements EventEmitter {
    Logger logger = Logger.getLogger(OrderEventProducer.class.getName());
    private KafkaAvroConfiguration configuration = null;
    private KafkaProducer<String, GenericRecord> kafkaProducer = null;
/*
    private SchemaRegistry schemaRegistry = null;
    private SchemaInfo schema = null;
  
    

    public OrderEventEventStreamsProducer() {
        super();
        configuration = new AvroKafkaConfiguration();
        Properties props = configuration.getAvroProducerProperties("OrderProducer_" + UUID.randomUUID());
            // Get a new connection to the Schema Registry
            try {
                schemaRegistry = new SchemaRegistry(props);
                // Get the schema from the registry
                schema = schemaRegistry.getSchema(configuration.schemaName,configuration.schemaVersion);
            } catch (KeyManagementException | NoSuchAlgorithmException | SchemaNotFoundException
                    | PropertyNotFoundException | InvalidPropertyValueException | SchemaRegistryAuthException
                    | SchemaRegistryServerErrorException | SchemaRegistryApiException
                    | SchemaRegistryConnectionException e) {
               
                e.printStackTrace();
            }
    }

    public void sendOrderEvents(List<OrderEvent> l) {
        for (OrderEvent t : l) {
            emit(t);
        }
    }
*/
    public void emit(OrderEventOld oevent) { 
            /*
          GenericRecord genericRecord = buildGenericRecord(oevent);
        
        ProducerRecord<String, GenericRecord> producerRecord = new ProducerRecord<String, GenericRecord>(
                configuration.getTopicName(), oevent.getPayload().getOrderID(), genericRecord);
        producerRecord.headers().add(SchemaRegistryConfig.HEADER_MSG_SCHEMA_ID, schema.getIdAsBytes());
        producerRecord.headers().add(SchemaRegistryConfig.HEADER_MSG_SCHEMA_VERSION, schema.getVersionAsBytes());

        logger.info("sending to " + configuration.getTopicName() + " item " + producerRecord
        .toString() + "\n with schema " + configuration.schemaName + " " + configuration.schemaVersion);
    
        try {
            getProducer().send(producerRecord, new Callback() {

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
        */
    }

    public KafkaProducer<String, GenericRecord> getProducer() {
        if (kafkaProducer == null) {
            kafkaProducer = new KafkaProducer<String, GenericRecord>(configuration.getProducerProperties("OrderProducer_" + UUID.randomUUID()));
        }
        return kafkaProducer;
    }


    private GenericRecord buildGenericRecord(OrderEventOld oevent) {
        GenericRecord genericRecord=null;
        /*
        GenericRecord genericRecord = new GenericData.Record(schema.getSchema());
    
        genericRecord.put("lane_id", oevent.lane_id);
        genericRecord.put("from_loc", oevent.from_loc);
        genericRecord.put("to_loc", oevent.to_loc);
        genericRecord.put("fixed_cost", oevent.fixed_cost);
        genericRecord.put("reefer_cost", oevent.reefer_cost);
        genericRecord.put("transit_time", oevent.transit_time);
        */
        return genericRecord;
    }

    @Override
    public void safeClose() {
        kafkaProducer.close();
        kafkaProducer = null;

    }
}
