package ibm.eda.demo.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ibm.eda.demo.app.dto.OrderDTO;
import ibm.eda.demo.domain.Order;
import ibm.eda.demo.domain.OrderService;

/**
 * Order Resource controller, it should not include business logic
 */
@RestController
@RequestMapping("/orders")
public class OrderResource {

    @Autowired
    public OrderService orderService;
    
    public OrderResource() {
        super();
    }


    @PostMapping
    public OrderDTO saveOrder(@RequestBody OrderDTO orderRequest) {
        Order aNewOrder = Order.from(orderRequest);
        return orderService.processANewOrder(aNewOrder).toDTO();
    }

    @GetMapping
    @RequestMapping("/{id}")
    public OrderDTO getOrderById(@RequestParam(value= "id") String id) {
        return orderService.getOrderById(id).toDTO();
    }

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> dtos = new ArrayList<OrderDTO>();
        for (Order o : orderService.getAllOrders()){
            dtos.add(o.toDTO());
        }
        return dtos;
    }

}