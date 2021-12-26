package ibm.eda.demo.ordermgr.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import ibm.eda.demo.ordermgr.infra.events.Address;
import ibm.eda.demo.ordermgr.infra.events.OrderEvent;
import ibm.eda.demo.ordermgr.infra.repo.OrderRepository;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;


@ApplicationScoped
public class OrderService {
	private static final Logger logger = Logger.getLogger(OrderService.class.getName());

	@Inject
	public OrderRepository repository;
    @Channel("orders")
	public Emitter<OrderEvent> eventProducer;
	
	public OrderService(){}
	
	public OrderEntity createOrder(OrderEntity order) {
		if (order.creationDate == null) {
			order.creationDate = LocalDate.now().toString();
		}
		order.updateDate= order.creationDate;
		repository.addOrder(order);
		Address deliveryAddress = new Address(order.getDeliveryAddress().getStreet()
				,order.getDeliveryAddress().getCity()
				,order.getDeliveryAddress().getCountry()
				,order.getDeliveryAddress().getState(),
				order.getDeliveryAddress().getZipcode());
		OrderEvent orderPayload =
		 new OrderEvent(order.getOrderID(),
				order.getProductID(),
				order.getCustomerID(),
				order.getQuantity(),
				order.getStatus(),
		        order.creationDate,
				order.updateDate,
				deliveryAddress,
				"OrderCreatedEvent");	
		try {
			
			Message<OrderEvent> record = KafkaRecord.of(order.getOrderID(),orderPayload);
			eventProducer.send(record);
			logger.info("order created event sent for " + order.getOrderID());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return order;
	}

	public List<OrderEntity> getAllOrders() {
		return repository.getAll();
	}

    public OrderEntity updateOrder(OrderEntity order) {
		order.updateDate = LocalDate.now().toString();
		repository.updateOrder(order);
		Address deliveryAddress = null;
		if (order.getDeliveryAddress() !=null) {
			deliveryAddress = new Address(order.getDeliveryAddress().getStreet()
			,order.getDeliveryAddress().getCity()
			,order.getDeliveryAddress().getCountry()
			,order.getDeliveryAddress().getState(),
			order.getDeliveryAddress().getZipcode());
		}
		
		OrderEvent orderPayload =
		 new OrderEvent(order.getOrderID(),
				order.getProductID(),
				order.getCustomerID(),
				order.getQuantity(),
				order.getStatus(),
		        order.creationDate,
				order.updateDate,
				deliveryAddress,
				"OrderUpdateEvent");	
			try {
				logger.info("emit order updated event for " + order.getOrderID());
				eventProducer.send(orderPayload);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return order;
    }

	
}
