package ibm.eda.demo.ordermgr.infra.api.dto;

import com.fasterxml.jackson.databind.util.BeanUtil;

import ibm.eda.demo.ordermgr.domain.Address;
import ibm.eda.demo.ordermgr.domain.OrderEntity;

public class OrderDTO {
	public String orderID;
	public String customerID;
	public String productID;
	public int quantity;
	public Address destinationAddress;
	
	
	public OrderDTO() {
		// needed for jaxrs serialization
	}
	 
	public OrderDTO(String customerID, String productID, int quantity, Address destinationAddress) {
		super();
		this.customerID = customerID;
		this.productID = productID;
		this.quantity = quantity;
		this.destinationAddress = destinationAddress;
	}
	
	public static OrderDTO from(OrderEntity order){
		OrderDTO dto = new OrderDTO();
		dto.orderID = order.orderID;
		dto.customerID = order.customerID;
		dto.productID = order.productID;
		dto.destinationAddress = order.deliveryAddress;
		dto.quantity = order.quantity;
		return dto;
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
	public Address getDestinationAddress() {
		return destinationAddress;
	}
	public void setDestinationAddress(Address destinationAddress) {
		this.destinationAddress = destinationAddress;
	}
}
