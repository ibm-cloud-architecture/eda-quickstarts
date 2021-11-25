package ibm.eda.demo.ordermgr.infra.events;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Serializer;

public class OrderSerializer implements Serializer<OrderEvent> {
    @Override public void close() {

    }
  
    @Override public void configure(Map<String, ?> arg0, boolean arg1) {
  
    }
  
    @Override
    public byte[] serialize(String arg0, OrderEvent arg1) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
        retVal = objectMapper.writeValueAsString(arg1).getBytes();
        } catch (Exception e) {
        e.printStackTrace();
        }
        return retVal;
    }
}
