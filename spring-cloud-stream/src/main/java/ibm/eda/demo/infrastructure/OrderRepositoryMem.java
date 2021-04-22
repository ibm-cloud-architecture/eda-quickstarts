package ibm.eda.demo.infrastructure;

import java.util.HashMap;

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
    
    
    
}
