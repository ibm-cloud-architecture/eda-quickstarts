package ut;

import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import ibm.eda.demo.ordermgr.domain.Address;
import ibm.eda.demo.ordermgr.domain.OrderEntity;
import ibm.eda.demo.ordermgr.domain.OrderService;
import ibm.eda.demo.ordermgr.infra.KafkaConfiguration;
import ibm.eda.demo.ordermgr.infra.OrderEventProducer;
import ibm.eda.demo.ordermgr.infra.OrderRepositoryMem;
import it.eda.StrimziContainer;

public class TestService {
    
    @ClassRule
    public static StrimziContainer kafka = new StrimziContainer();
    @ClassRule
    public static GenericContainer apicurio = new GenericContainer(DockerImageName.parse("apicurio/apicurio-registry-mem"));
    public static OrderService service = null;

    @BeforeAll
    public static void init(){
        OrderRepositoryMem repo = new OrderRepositoryMem();
        apicurio.addExposedPort(8090);
        KafkaConfiguration configuration = new KafkaConfiguration(kafka.getBootstrapServers(),apicurio.getContainerIpAddress());
        OrderEventProducer eventProducer = new OrderEventProducer(configuration);
        service = new OrderService(eventProducer, repo);
    }

    @Test
    public void testLoadingOrders(){
        Assertions.assertTrue(service.getAllOrders().size() == 2);
    }

    @Test
    public void shouldAddAnOrder(){
        
        Address customerAddress = new Address("main street", "TestCity", "USA","CA","95000");
        OrderEntity order = new OrderEntity("TestOrder01","Product01","Customer01", 10, customerAddress, OrderEntity.PENDING_STATUS);
        service.createOrder(order);
        Assertions.assertTrue(service.getAllOrders().size() == 3);
    }
}
