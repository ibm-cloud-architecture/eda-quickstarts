package ibm.eda.demo.ordermgr.infra.events;

public interface EventEmitter {

    public void emit(OrderEvent event) throws Exception;
    public void safeClose();

}
