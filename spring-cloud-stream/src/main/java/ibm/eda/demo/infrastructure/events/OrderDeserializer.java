package ibm.eda.demo.infrastructure.events;

import org.springframework.kafka.support.serializer.JsonDeserializer;

import ibm.eda.demo.domain.Order;

public class OrderDeserializer extends JsonDeserializer<Order> {
    
}
