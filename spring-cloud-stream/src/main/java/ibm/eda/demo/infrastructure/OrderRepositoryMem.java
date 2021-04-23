package ibm.eda.demo.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Repository;

import ibm.eda.demo.domain.Order;

@Repository
public class OrderRepositoryMem implements OrderRepository {

    private static HashMap<String,Order> repo = new HashMap<String,Order>();

    @Override
    public Order saveOrder(Order order) {
        repo.put(order.orderID, order);
        return order;
    }

    @Override
    public Order getOrderById(String orderID) {
        return repo.get(orderID);
    }
    
    
    @Bean
    public Consumer<Message<Order>> consumeOrderEvent(){
        return msg -> {
            Acknowledgment acknowledgment = msg.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);
            saveOrder(msg.getPayload());
            if (acknowledgment != null) {
                System.out.println("Acknowledgment provided");
                acknowledgment.acknowledge();
            }
        };
    }

    @Override
    public List<Order> getAllOrders() {
        return new ArrayList<Order>(repo.values());
    }
    
}
