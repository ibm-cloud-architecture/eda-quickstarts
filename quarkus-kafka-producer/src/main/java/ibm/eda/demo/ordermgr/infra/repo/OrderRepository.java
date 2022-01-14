package ibm.eda.demo.ordermgr.infra.repo;

import java.util.List;

import ibm.eda.demo.ordermgr.domain.OrderEntity;

public interface OrderRepository {
    public List<OrderEntity> getAll();
    public void addOrder(OrderEntity entity);
    public void updateOrder(OrderEntity entity);
    public OrderEntity findById(String key);
}
