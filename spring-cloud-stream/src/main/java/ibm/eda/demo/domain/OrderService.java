package ibm.eda.demo.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import ibm.eda.demo.infrastructure.OrderRepository;

@Service
public class OrderService {
    public static String BINGING_NAME = "orders-out-0";
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
	private StreamBridge streamBridge;

    public OrderService() {
        super();
    }

    public Order processANewOrder(Order order) {
        order.status = OrderStatus.OPEN;
        streamBridge.send(BINGING_NAME, order);
        return order;
    }

    public Order getOrderById(String id) {
        return orderRepository.getOrderById(id);
    }

    
 }
