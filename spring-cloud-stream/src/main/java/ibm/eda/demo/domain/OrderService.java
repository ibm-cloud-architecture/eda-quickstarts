package ibm.eda.demo.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import ibm.eda.demo.infrastructure.OrderRepository;
import ibm.eda.demo.infrastructure.events.CloudEvent;

@Service
public class OrderService {
    public static String BINDING_NAME = "orders-out-0";
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
	private StreamBridge streamBridge;

    public OrderService() {
        super();
    }

    public Order processANewOrder(Order order) {
        order.status = OrderStatus.OPEN;
        order.orderID = UUID.randomUUID().toString();
        order.creationDate = LocalDate.now();

        Message<CloudEvent> toSend = MessageBuilder.withPayload(prepareCloudEvent(order))
            .setHeader(KafkaHeaders.MESSAGE_KEY, order.customerID.getBytes())
            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build();
        streamBridge.send(BINDING_NAME, toSend);
        return order;
    }

    public Order getOrderById(String id) {
        return orderRepository.getOrderById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }
    
    public CloudEvent prepareCloudEvent(Order order) {
        CloudEvent ce = new CloudEvent();
        ce.setType(Order.class.getName());
        ce.setTime(LocalDate.now().toString());
        ce.setSpecversion("0.2");
        ce.setContenttype("value");
        ce.setData(order);
        return ce;
    }
 }
