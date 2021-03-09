package ibm.eda.demo.app.api;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ibm.eda.demo.app.api.dto.OrderDTO;
import ibm.eda.demo.app.domain.OrderEntity;
import ibm.eda.demo.app.domain.OrderService;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class OrderResource {

    @Inject
    public OrderService service;
    
    @GET
    
    public List<OrderDTO> getAllActiveOrders() {
        List<OrderDTO> l = new ArrayList<OrderDTO>();
        for (OrderEntity order : service.getAllOrders()) {
            l.add(OrderDTO.from(order));
        }
        return l;
    }

    @POST
    public void saveNewOrder(OrderDTO order) {
        OrderEntity entity = OrderEntity.from(order);
        service.createOrder(entity);
    }
    
}