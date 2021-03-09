package ut;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ibm.eda.demo.app.domain.OrderService;
import ibm.eda.demo.app.infrastructure.OrderEventProducer;
import ibm.eda.demo.app.infrastructure.OrderRepositoryMem;

public class TestService {
    
    @Test
    public void testLoadingOrders(){
        OrderRepositoryMem repo = new OrderRepositoryMem();
        OrderEventProducer eventProducer = new OrderProducerMock();

        OrderService service = new OrderService(eventProducer, repo);
        Assertions.assertTrue(service.getAllOrders().size() == 2);
    }
}
