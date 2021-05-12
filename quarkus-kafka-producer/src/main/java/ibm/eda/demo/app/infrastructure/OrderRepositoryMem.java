package ibm.eda.demo.app.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

import ibm.eda.demo.app.domain.OrderEntity;

@Singleton
public class OrderRepositoryMem {
    private static HashMap<String,OrderEntity> repo = new HashMap<String,OrderEntity>();

    private static ObjectMapper mapper = new ObjectMapper();
    

    public OrderRepositoryMem() {
        super();
        InputStream is = getClass().getClassLoader().getResourceAsStream("orders.json");
        if (is == null) 
            throw new IllegalAccessError("file not found for order json");
        try {
            List<OrderEntity> currentTransportationDefinitions = mapper.readValue(is, mapper.getTypeFactory().constructCollectionType(List.class, OrderEntity.class));
            currentTransportationDefinitions.stream().forEach( (t) -> repo.put(t.getOrderID(),t));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<OrderEntity> getAll(){
        return new ArrayList<OrderEntity>(repo.values());
    }

    public void addOrder(OrderEntity entity) {
        repo.put(entity.getOrderID(), entity);
    }

    public void updateOrder(OrderEntity entity) {
        repo.put(entity.getOrderID(), entity);
    }
}
