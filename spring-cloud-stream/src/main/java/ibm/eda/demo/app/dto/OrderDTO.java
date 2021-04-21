package ibm.eda.demo.app.dto;

import java.time.LocalDate;
/**
 * Order view for REST resource. could be one to one with domain object, in this case, do not use DTO class.
 */
public class OrderDTO {
    public String orderID;
	

    public String customerID;
	public String productID;
	public int quantity;
    public LocalDate creationDate;

    public OrderDTO() {
        super();
    }

    public OrderDTO(String customerID, String product, int quantity) {
        this.customerID = customerID;
        this.productID = product;
        this.quantity = quantity;
        this.creationDate = LocalDate.now();
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}
