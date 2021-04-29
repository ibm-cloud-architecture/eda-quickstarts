package ibm.eda.demo.infrastructure.events;

import org.springframework.kafka.support.serializer.JsonDeserializer;

public class CloudEventDeserializer extends JsonDeserializer<CloudEvent> {
    
}
