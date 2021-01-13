package ibm.tsa.eda.demo.domain.infrastructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Singleton;

import ibm.tsa.eda.demo.domain.Product;

@Singleton
public class ProductRepository {
    
    private static HashMap<String,Product> repo = new HashMap<String,Product>();

    public ProductRepository() {
        super();
    }

    public List<Product> getAll(){
        return new ArrayList<Product>(repo.values());
    }

    public void addProduct(Product p) {
        repo.put(p.product_id, p);
    }
}
