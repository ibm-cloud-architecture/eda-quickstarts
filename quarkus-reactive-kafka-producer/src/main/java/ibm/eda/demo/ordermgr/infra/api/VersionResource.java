package ibm.eda.demo.ordermgr.infra.api;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import ibm.eda.demo.ordermgr.domain.OrderService;
import io.quarkus.runtime.StartupEvent;

@Path("/api/v1/version")
@ApplicationScoped
public class VersionResource {
    private static final Logger logger = Logger.getLogger(OrderService.class.getName());

    @Inject
    @ConfigProperty(name="app.version")
    public String version;

    @GET
    public String getVersion(){
        return "{ \"version\": \"" + version + "\"}";
    }

    void onStart(@Observes StartupEvent ev){
		logger.info(getVersion());
	}
}
