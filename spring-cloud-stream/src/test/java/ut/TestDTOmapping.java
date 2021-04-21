package ut;

import java.time.LocalDate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ibm.eda.demo.app.dto.OrderDTO;
import ibm.eda.demo.domain.Order;
import ibm.eda.demo.domain.OrderStatus;

public class TestDTOmapping {
  
    @Test
    public void shouldMapDTOtoDomain(){
        OrderDTO dto = new OrderDTO("CUST_01","PROD_01",10);
        Order order = Order.from(dto);
        Assertions.assertEquals(dto.customerID, order.customerID);
        Assertions.assertEquals(dto.productID, order.productID);
    }

    @Test
    public void shouldMapDomainToDTO(){
        Order order = new Order();
        order.setCustomerID("CUST_02");
        order.setProductID("PROD_02");
        order.setStatus(OrderStatus.PENDING);
        order.setCreationDate(LocalDate.now());
        OrderDTO dto = order.toDTO();
        Assertions.assertEquals(order.customerID, dto.customerID);
        Assertions.assertEquals(order.productID, dto.productID);
        Assertions.assertEquals(order.getCreationDate(), dto.getCreationDate());
    }
}
