package ibm.eda.demo.app.domain;

import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ibm.eda.demo.app.infrastructure.OrderEventProducer;
import ibm.eda.demo.app.infrastructure.OrderRepositoryMem;
import ibm.eda.demo.app.infrastructure.events.OrderEventOld;
import ibm.eda.demo.app.infrastructure.events.OrderPayload;

@ApplicationScoped
public class OrderService {
	private static final Logger logger = Logger.getLogger(OrderService.class.getName());

	@Inject
	public OrderRepositoryMem repository;

	@Inject
	public OrderEventProducer eventProducer;
	
	
	public OrderService(OrderEventProducer eventProducer, OrderRepositoryMem  repo) {
		this.eventProducer = eventProducer;
		this.repository = repo;
	}
	
	public void createOrder(OrderEntity order) {
		// TODO This has to be transactional or use outbox, or use command pattern to kafka topic
		repository.addOrder(order);
		OrderEventOld orderEvent = new OrderEventOld(System.currentTimeMillis(),
				OrderEventOld.TYPE_ORDER_CREATED);
		OrderPayload orderPayload = new OrderPayload(order.getOrderID(),
				order.getProductID(),
				order.getCustomerID(),
				order.getQuantity(),
				order.getStatus(),
				order.getDeliveryAddress());
		orderEvent.setPayload(orderPayload);
		try {
			logger.severe("emit event for " + order.getOrderID());
			eventProducer.emit(orderEvent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<OrderEntity> getAllOrders() {
		return repository.getAll();
	}

}
