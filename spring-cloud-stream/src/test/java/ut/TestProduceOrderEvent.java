package ut;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class TestProduceOrderEvent {
    

    @Test
    public void givenEmbeddedKafka_whenSendingOrder_thenOrderEventSent(){
        
    }
}
