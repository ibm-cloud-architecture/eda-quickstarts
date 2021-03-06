package ibm.eda.demo.infrastructure;

import java.util.List;

import ibm.eda.demo.domain.Order;

public interface OrderRepository {
    
    public Order saveOrder(Order order);

    public Order getOrderById(String orderID);

    public List<Order> getAllOrders();
}
