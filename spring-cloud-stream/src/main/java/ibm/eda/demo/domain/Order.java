package ibm.eda.demo.domain;

import java.time.LocalDate;

import org.springframework.beans.BeanUtils;

import ibm.eda.demo.app.dto.OrderDTO;

public class Order {
    public String orderID;
	public String customerID;
	public String productID;
	public int quantity;
    public OrderStatus status;
    public LocalDate creationDate;

    public Order() {
        super();
    }

    public static Order from(OrderDTO dto){
       
        Order mappedOrder = new Order();
        BeanUtils.copyProperties(dto, mappedOrder);
        return mappedOrder;
    }

    public  OrderDTO toDTO() {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    
}
