package ibm.eda.demo.ordermgr.infra.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import ibm.eda.demo.ordermgr.domain.OrderEntity;
import ibm.eda.demo.ordermgr.domain.OrderService;
import ibm.eda.demo.ordermgr.infra.api.dto.OrderDTO;
import io.smallrye.mutiny.Uni;

@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class OrderResource {
    private static final Logger logger = Logger.getLogger(OrderResource.class.getName());

    @Inject
    public OrderService service;
    
    @GET
    public List<OrderDTO> getAllActiveOrders() {
        List<OrderDTO> l = new ArrayList<OrderDTO>();
        for (OrderEntity order : service.getAllOrders()) {
            l.add(OrderDTO.fromEntity(order));
        }
        return l;
    }


    @GET
    @Path("/{id}")
    public OrderDTO get(@PathParam("id") String id) {
        logger.info("In get order with id: " + id);
        OrderEntity order = service.findById(id);
        if (order == null) {
            throw new WebApplicationException("Order with id of " + id + " does not exist.", 404);
     
        }
        return OrderDTO.fromEntity(order);
    }

    @POST
    public OrderDTO saveNewOrder(OrderDTO order) {
        OrderEntity entity = OrderDTO.toEntity(order);
        return OrderDTO.fromEntity(service.createOrder(entity));
    }

    @PUT
    public void updateExistingOrder(OrderDTO order) {
        OrderEntity entity = OrderEntity.from(order);
        service.updateOrder(entity);
    }
    
}