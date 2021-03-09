package ibm.eda.demo.app.infrastructure.events;

import ibm.eda.demo.app.domain.Address;

public class OrderPayload {
	private String orderID;
    private String productID;
    private String customerID;
    private int quantity;
    private String status;
    private Address shippingAddress;
    
    public OrderPayload() {
    	// needed for deserialization
    }
    
	public OrderPayload(String orderID, String productID, String customerID, int quantity, String status,
			Address shippingAddress) {
		super();
		this.orderID = orderID;
		this.productID = productID;
		this.customerID = customerID;
		this.quantity = quantity;
		this.status = status;
		this.shippingAddress = shippingAddress;
	}
	
	public String getOrderID() {
		return orderID;
	}
	
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getCustomerID() {
		return customerID;
	}
	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Address getShippingAddress() {
		return shippingAddress;
	}
	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}
}
