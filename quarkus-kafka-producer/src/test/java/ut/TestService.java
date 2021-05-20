package ut;

import org.junit.ClassRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import ibm.eda.demo.ordermgr.domain.OrderEntity;
import ibm.eda.demo.ordermgr.domain.OrderService;
import ibm.eda.demo.ordermgr.infra.KafkaConfiguration;
import ibm.eda.demo.ordermgr.infra.OrderEventProducer;
import ibm.eda.demo.ordermgr.infra.OrderRepositoryMem;

public class TestService {
    
    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("strimzi/kafka:latest-kafka-2.6.0"));
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
        OrderEntity order = null;
        service.createOrder(order);
    }
}
