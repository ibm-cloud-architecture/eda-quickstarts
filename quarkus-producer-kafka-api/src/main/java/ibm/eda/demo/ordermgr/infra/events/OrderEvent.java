package ibm.eda.demo.ordermgr.infra.events;

public class OrderEvent {
  public java.lang.String orderID;
  /** time stamp of the order creation */
  public long timestampMillis;
  /** Type of event */
  public ibm.eda.demo.ordermgr.infra.events.EventType type;
  /** Different payload structure depending of event type */
  public java.lang.Object payload;

  public OrderEvent(){}

  public OrderEvent(String orderID, 
        long currentTimeMillis, 
        EventType eventType, 
        Object orderPayload) {
        this.orderID= orderID;
        this.type = eventType;
        this.payload=orderPayload;
        this.timestampMillis = currentTimeMillis;
  }

  public String toString(){
      return "OrderEvent: { orderid: " + this.orderID 
        + ", type: " + this.type
        + ", payload: " + this.payload.toString() 
        + "}";
  } 
}
