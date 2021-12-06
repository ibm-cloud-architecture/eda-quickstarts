package ibm.eda.demo.ordermgr.domain;

import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import ibm.eda.demo.ordermgr.infra.events.Address;
import ibm.eda.demo.ordermgr.infra.events.OrderCloudEvent;
import ibm.eda.demo.ordermgr.infra.events.OrderCreatedEvent;
import ibm.eda.demo.ordermgr.infra.repo.OrderRepository;


@ApplicationScoped
public class OrderService {
	private static final Logger logger = Logger.getLogger(OrderService.class.getName());

	@Inject
	public OrderRepository repository;
    @Channel("orders")
	public Emitter<OrderCloudEvent> eventProducer;
	
	public OrderService(){}
	
	public OrderEntity createOrder(OrderEntity order) {
		repository.addOrder(order);
		Address deliveryAddress = new Address(order.getDeliveryAddress().getStreet()
				,order.getDeliveryAddress().getCity()
				,order.getDeliveryAddress().getCountry()
				,order.getDeliveryAddress().getState(),
				order.getDeliveryAddress().getZipcode());
		OrderCreatedEvent orderPayload =
		 new OrderCreatedEvent(order.getOrderID(),
				order.getProductID(),
				order.getCustomerID(),
				order.getQuantity(),
				order.getStatus(),
				deliveryAddress);
		OrderCloudEvent orderEvent = new OrderCloudEvent(
			"ibm.eda.demo.ordermgr.infra.events.OrderCloudEvent",
			"1.0.0",
			"OrderManagerService",
			order.getOrderID(),
			Instant.now().toString(),
			"ibm.eda.demo.ordermgr.infra.events",
			"ibm.eda.demo.ordermgr.infra.events.OrderCreatedEvent",
				orderPayload);		
		try {
			logger.info("emit event for " + order.getOrderID());
			eventProducer.send(orderEvent);
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
