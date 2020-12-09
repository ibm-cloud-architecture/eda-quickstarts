package ibm.tsa.eda.app;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Path("/kafka")
public class Producer {
    // use connector my-out-going to create channel to send data
    // my-out-going connector is configured in applicaition.properties
    // we can only send Double data to double emmiter because of Double type and serialization configured on `my-out-going` on application.properties
    @Inject @Channel("my-out-going") Emitter<Double> doubleEmitter;
    
    @GET
    @Path("produce")
    public void sendMessage() {
        // send message 1.3 to doubleEmitter
        doubleEmitter.send(1.3);
    }

}
