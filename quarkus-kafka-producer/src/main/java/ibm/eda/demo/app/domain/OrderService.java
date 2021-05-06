package ibm.eda.demo.app.domain;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ibm.eda.demo.app.infrastructure.OrderRepositoryMem;
import ibm.eda.demo.app.infrastructure.events.Address;
import ibm.eda.demo.app.infrastructure.events.EventEmitter;
import ibm.eda.demo.app.infrastructure.events.EventType;
import ibm.eda.demo.app.infrastructure.events.OrderCreatedEvent;
import ibm.eda.demo.app.infrastructure.events.OrderEvent;

@ApplicationScoped
public class OrderService {
	private static final Logger logger = Logger.getLogger(OrderService.class.getName());

	@Inject
	public OrderRepositoryMem repository;

	@Inject
	@Named("avroProducer")
	public EventEmitter eventProducer;
	
	public OrderService(){}

	public OrderService(EventEmitter eventProducer, OrderRepositoryMem  repo) {
		this.eventProducer = eventProducer;
		this.repository = repo;
	}
	
	public void createOrder(OrderEntity order) {
		// TODO This has to be transactional or use outbox, or use command pattern to kafka topic
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
	}

	public List<OrderEntity> getAllOrders() {
		return repository.getAll();
	}

}
