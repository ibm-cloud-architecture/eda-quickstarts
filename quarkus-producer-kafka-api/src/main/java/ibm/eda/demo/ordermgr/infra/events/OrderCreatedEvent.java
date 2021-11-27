package ibm.eda.demo.ordermgr.infra.events;

import ibm.eda.demo.ordermgr.domain.Address;

public class OrderCreatedEvent {
  
  /** Unique ID from source system */
   private java.lang.String orderID;
  /** Unique ID for the product as defined in product catalog */
   private java.lang.String productID;
  /** Unique ID for the customer organization */
   private java.lang.String customerID;
  /** Quantity ordered */
   private int quantity;
  /** Status of the order. */
   private java.lang.String status;
  /** Address to ship the ordered items */
   private Address shippingAddress;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public OrderCreatedEvent() {}

  /**
   * All-args constructor.
   * @param orderID Unique ID from source system
   * @param productID Unique ID for the product as defined in product catalog
   * @param customerID Unique ID for the customer organization
   * @param quantity Quantity ordered
   * @param status Status of the order.
   * @param shippingAddress Address to ship the ordered items
   */
  public OrderCreatedEvent(String orderID, 
    String productID, 
    String customerID, 
    int quantity, 
    String status, 
    Address shippingAddress) {
      this.orderID = orderID;
      this.productID = productID;
      this.customerID = customerID;
      this.quantity = quantity;
      this.status = status;
      this.shippingAddress = shippingAddress;
  }

  public String toString(){
    return "OrderCreateEvent: { orderid: " + this.orderID + ", customer: " + this.customerID + ", product: " + this.productID + "}";
  }
  /**
   * Gets the value of the 'orderID' field.
   * @return Unique ID from source system
   */
  public java.lang.String getOrderID() {
    return orderID;
  }


  /**
   * Sets the value of the 'orderID' field.
   * Unique ID from source system
   * @param value the value to set.
   */
  public void setOrderID(java.lang.String value) {
    this.orderID = value;
  }

  /**
   * Gets the value of the 'productID' field.
   * @return Unique ID for the product as defined in product catalog
   */
  public java.lang.String getProductID() {
    return productID;
  }


  /**
   * Sets the value of the 'productID' field.
   * Unique ID for the product as defined in product catalog
   * @param value the value to set.
   */
  public void setProductID(java.lang.String value) {
    this.productID = value;
  }

  /**
   * Gets the value of the 'customerID' field.
   * @return Unique ID for the customer organization
   */
  public java.lang.String getCustomerID() {
    return customerID;
  }


  /**
   * Sets the value of the 'customerID' field.
   * Unique ID for the customer organization
   * @param value the value to set.
   */
  public void setCustomerID(java.lang.String value) {
    this.customerID = value;
  }

  /**
   * Gets the value of the 'quantity' field.
   * @return Quantity ordered
   */
  public int getQuantity() {
    return quantity;
  }


  /**
   * Sets the value of the 'quantity' field.
   * Quantity ordered
   * @param value the value to set.
   */
  public void setQuantity(int value) {
    this.quantity = value;
  }

  /**
   * Gets the value of the 'status' field.
   * @return Status of the order.
   */
  public java.lang.String getStatus() {
    return status;
  }


  /**
   * Sets the value of the 'status' field.
   * Status of the order.
   * @param value the value to set.
   */
  public void setStatus(java.lang.String value) {
    this.status = value;
  }

  /**
   * Gets the value of the 'shippingAddress' field.
   * @return Address to ship the ordered items
   */
  public Address getShippingAddress() {
    return shippingAddress;
  }


  /**
   * Sets the value of the 'shippingAddress' field.
   * Address to ship the ordered items
   * @param value the value to set.
   */
  public void setShippingAddress(Address value) {
    this.shippingAddress = value;
  }
}










