package ibm.eda.demo.app.infrastructure.events;

public interface EventEmitter {

    public void emit(OrderEventOld event) throws Exception;
    public void safeClose();

}
