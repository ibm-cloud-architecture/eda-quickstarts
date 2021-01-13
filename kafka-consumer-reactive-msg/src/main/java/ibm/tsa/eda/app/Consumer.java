package ibm.tsa.eda.app;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class Consumer {

/**
 * Here we are using MicroProfile Reactive Messaging to interact with Apache Kafka. 
 * We have setup all configuration required to read data from a topic in application.properties file 
 * all configuration starting with mp.messaging.incoming.my-data-stream is for this incoming connection
 * all configuration starting with mp.messaging.connector.smallrye-kafka is also for this connection 
 * because we have configured `mp.messaging.incoming.my-data-stream.connector=smallrye-kafka`
 * so in this function we provide connector to use same as our connecore above. `my-data-stream` and it is incoming because of `incoming`
 * @param priceInUsd data sent to topic. data sent to topic should be of type double otherwise deserialization will fail
 * @return 
 */
    @Incoming("my-data-stream")                    
    public void process(double priceInUsd) {
        // simple pring
        System.out.println("-Message Received! from kafa-");
        System.out.println(priceInUsd);
    }
}
