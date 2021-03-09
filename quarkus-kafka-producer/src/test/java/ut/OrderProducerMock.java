package ut;

import javax.enterprise.context.ApplicationScoped;

import ibm.eda.demo.app.infrastructure.OrderEventProducer;
import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
public class OrderProducerMock extends OrderEventProducer {
    
    public OrderProducerMock() {
    }
}
