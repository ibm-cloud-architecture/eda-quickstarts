package ibm.tsa.eda.demo.domain;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.logging.Logger;

import ibm.tsa.eda.demo.infrastructure.ProductConsumer;
import ibm.tsa.eda.demo.infrastructure.ProductRepository;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@Singleton
public class ProductService {
    private static final Logger logger = Logger.getLogger(ProductService.class.getName());
    private ExecutorService executor;
    private static int MAX_POOL_SIZE = 1;

    ProductRepository repository;
    ProductConsumer productConsumer;

    public ProductService() {
        super();
    }

	public List<Product> getProducts() {
		return repository.getAll();
    }

    void onStart(@Observes StartupEvent ev) {               
        logger.info("The application " 
            + ConfigProvider.getConfig().getValue("quarkus.application.name",String.class)
            + " v="
            + ConfigProvider.getConfig().getValue("quarkus.application.version",String.class) 
            + " is starting...");
        executor = Executors.newFixedThreadPool(MAX_POOL_SIZE);
        repository = new ProductRepository();
        productConsumer = new ProductConsumer(repository);
        executor.execute(productConsumer);
    }

    void onStop(@Observes ShutdownEvent ev) {               
        logger.info("The application is stopping...");
        productConsumer.stop();
        executor.shutdown();
    }
    
   
}
