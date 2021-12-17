
package ibm.eda.demo.kafka.producer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import java.util.logging.Logger;
import org.jboss.logging.annotations.Param;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;

@ApplicationScoped
@Path("/api/v1/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DemoController {
    Logger logger = Logger.getLogger(DemoController.class.getName());
    @Inject
    @Channel("orders")
    Emitter<Order> emitter;

    private Random random = new Random();
    private String[] stores = {"Store_1","Store_2","Store_3"};
    private String[] products = {"MobilePhone","Laptop","Raspberry","Computer","Banana"};
    private Double[] prices  = { 500.00,1500.00,30.00,2000.00,0.98};

    @Inject
    @ConfigProperty(name="app.version")
    public String version;

    @GET
    @Path("/version")
    public String getVersion(){
        return "{ \"version\": \"" + version + "\"}";
    }
    
    @POST
    @Path("/start")
    public Uni<String> startDemo(@Param int numberOfRecords) {
      Multi.createFrom().items(buildOrders(numberOfRecords).stream())
      .subscribe().with( 
        item -> {
          Message<Order> record = KafkaRecord.of(item.store_num,item);
          emitter.send(record);
          logger.info("Send order to kafka");
          },
          failure -> {
            logger.severe("Failed with " + failure.getMessage());
            Uni.createFrom().item("{ \"status\": \"Failed\"}");
          });
        return Uni.createFrom().item("{ \"status\": \"Succeed\"}");
    }

     
    private List<Order> buildOrders(int numberOfRecords) {
      List<Order> orders = new ArrayList<Order>();
      for (int i=0; i<numberOfRecords;i++) {
        Order order = new Order();
        order.trans_num = i;
        order.trans_dt = LocalDate.now().toString();
        order.upc = products[random.nextInt(products.length)];
        order.quantity = random.nextInt(10);
        order.refund_ind = false;
        order.unit_price = prices[random.nextInt(prices.length)];
        order.store_num = stores[random.nextInt(stores.length)];
        orders.add(order);
      }
      return orders;
    }
}
