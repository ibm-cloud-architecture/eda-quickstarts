package ibm.eda.demo.app.infrastructure.events;

public class OrderEventOld {
	
	public static final String TYPE_ORDER_CREATED = "OrderCreated";
	public static final String TYPE_ORDER_UPDATED = "OrderUpdated";
	public static final String TYPE_ORDER_IN_TRANSIT = "OrderInTransit";
	public static final String TYPE_ORDER_COMPLETED = "OrderCompleted";
	public static final String TYPE_ORDER_REJECTED = "OrderRejected";
	public static final String TYPE_ORDER_CANCELLED = "OrderCancelled";
	 
	protected long timestampMillis;
    protected String type;
    protected OrderPayload payload;
    
    public OrderEventOld() {
    }

    public OrderEventOld(long timestampMillis, String type) {
        this.timestampMillis = timestampMillis;
        this.type = type;
     
    }

	public OrderPayload getPayload() {
		return payload;
	}

	public void setPayload(OrderPayload jsonPayload) {
		this.payload = jsonPayload;
	}

	public long getTimestampMillis() {
		return timestampMillis;
	}

	public String getType() {
		return type;
	}

}
