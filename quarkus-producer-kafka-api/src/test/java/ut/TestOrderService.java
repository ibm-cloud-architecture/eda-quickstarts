package ut;

import org.junit.jupiter.api.Test;

import ibm.eda.demo.ordermgr.domain.Address;
import ibm.eda.demo.ordermgr.domain.OrderEntity;
import ibm.eda.demo.ordermgr.domain.OrderService;
import ibm.eda.demo.ordermgr.infra.repo.OrderRepository;
import ibm.eda.demo.ordermgr.infra.repo.OrderRepositoryMem;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestOrderService {
 

    @Test
    public void testCreateOrder(){
        OrderRepository repo = new OrderRepositoryMem();
        OrderService service = new OrderService(new OrderProducerMock(),repo);
    
        Address deliveryAddress= new Address("main","santa","ca","USA","95051");
        OrderEntity orderEntity = new OrderEntity("Ord01",
        "P01",
        "Cust01",
        20,
        deliveryAddress,
        OrderEntity.PENDING_STATUS);
        service.createOrder(orderEntity);
    }
}
