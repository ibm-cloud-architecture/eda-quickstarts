package ibm.tsa.eda.demo.domain;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ibm.tsa.eda.demo.domain.infrastructure.ProductConsumer;
import ibm.tsa.eda.demo.domain.infrastructure.ProductRepository;

@ApplicationScoped
public class ProductService {

    private ExecutorService executor;
    private static int MAX_POOL_SIZE = 1;

    @Inject
    ProductRepository repository;

    public ProductService() {
        super();
    }

	public List<Product> getProducts() {
		return repository.getAll();
    }
    
    @PostConstruct
    public void prepareAndStartConsumer(){
        executor = Executors.newFixedThreadPool(MAX_POOL_SIZE);
        ProductConsumer runnable = new ProductConsumer();
        executor.execute(runnable);
    }
}
