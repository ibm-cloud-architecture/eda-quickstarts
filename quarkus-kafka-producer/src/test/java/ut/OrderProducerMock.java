package ut;

import javax.enterprise.context.ApplicationScoped;

import ibm.eda.demo.ordermgr.infra.events.OrderEvent;
import ibm.eda.demo.ordermgr.infra.kafka.OrderEventProducer;
import io.quarkus.test.Mock;

@Mock
@ApplicationScoped
public class OrderProducerMock extends OrderEventProducer {
    
    public OrderProducerMock() {
    }

    public void emit(OrderEvent oevent) {
        System.out.println(oevent.toString());
    };
}
