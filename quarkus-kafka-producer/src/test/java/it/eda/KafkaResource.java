package it.eda;

import java.util.Collections;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class KafkaResource implements QuarkusTestResourceLifecycleManager {

    private final StrimziContainer kafka = new StrimziContainer();
    @Override
    public Map<String, String> start() {
        kafka.start();
        return Collections.singletonMap("kafka.bootstrap.servers", kafka.getBootstrapServers());
    }

    @Override
    public void stop() {
        kafka.close();
        
    }
    
    public String getBootstrapServer(){
        return kafka.getBootstrapServers();
    } 
    
}
