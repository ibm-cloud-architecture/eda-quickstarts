package ibm.eda.demo.ordermgr.infra.api;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ibm.eda.demo.ordermgr.domain.OrderEntity;
import ibm.eda.demo.ordermgr.domain.OrderService;
import ibm.eda.demo.ordermgr.infra.api.dto.OrderDTO;

@Path("/api/v1/orders")
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
    public OrderDTO saveNewOrder(OrderDTO order) {
        OrderEntity entity = OrderEntity.from(order);
        return OrderDTO.from(service.createOrder(entity));
    }

    @PUT
    public void updateExistingOrder(OrderDTO order) {
        OrderEntity entity = OrderEntity.from(order);
        service.updateOrder(entity);
    }
    
}