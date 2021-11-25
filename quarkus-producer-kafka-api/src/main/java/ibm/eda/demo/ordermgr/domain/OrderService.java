package ibm.eda.demo.ordermgr.domain;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ibm.eda.demo.ordermgr.infra.events.EventType;
import ibm.eda.demo.ordermgr.infra.events.OrderCreatedEvent;
import ibm.eda.demo.ordermgr.infra.events.OrderEvent;
import ibm.eda.demo.ordermgr.infra.events.OrderEventProducer;
import ibm.eda.demo.ordermgr.infra.repo.OrderRepositoryMem;


@ApplicationScoped
public class OrderService {
	private static final Logger logger = Logger.getLogger(OrderService.class.getName());

	@Inject
	public OrderRepositoryMem repository;
    @Inject
	public OrderEventProducer eventProducer ;
	
	public OrderService(){}

	
	public OrderEntity createOrder(OrderEntity order) {
		repository.addOrder(order);
		Address deliveryAddress = new Address(order.getDeliveryAddress().getStreet()
				,order.getDeliveryAddress().getCity()
				,order.getDeliveryAddress().getCountry()
				,order.getDeliveryAddress().getState(),
				order.getDeliveryAddress().getZipcode());
		OrderCreatedEvent orderPayload = new OrderCreatedEvent(order.getOrderID(),
				order.getProductID(),
				order.getCustomerID(),
				order.getQuantity(),
				order.getStatus(),
				deliveryAddress);
		OrderEvent orderEvent = new OrderEvent(order.getOrderID(),System.currentTimeMillis(),
				EventType.OrderCreated,orderPayload);
		
		try {
			logger.info("emit event for " + order.getOrderID());
			eventProducer.emit(orderEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return order;
	}

	public List<OrderEntity> getAllOrders() {
		return repository.getAll();
	}

    public void updateOrder(OrderEntity entity) {
		repository.updateOrder(entity);
		// add update event
    }

}
