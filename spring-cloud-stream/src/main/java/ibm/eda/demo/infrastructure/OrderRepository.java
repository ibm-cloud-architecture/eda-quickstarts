package ibm.eda.demo.infrastructure;

import ibm.eda.demo.domain.Order;

public interface OrderRepository {
    
    public Order saveOrder(Order order);

    public Order getOrderById(String orderID);
}
