package ibm.eda.demo.ordermgr.infra.events;

import java.util.UUID;

import ibm.eda.demo.ordermgr.domain.OrderEntity;

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
        EventType eventType, OrderCreatedEvent orderPayload) {
        this.orderID= orderID;
        this.payload=orderPayload;
        this.timestampMillis = currentTimeMillis;
  }

  
}
