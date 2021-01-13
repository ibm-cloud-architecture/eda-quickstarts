package ut;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.jupiter.api.Test;

import ibm.tsa.eda.demo.domain.Product;

public class TestSerialization {
    
    @Test
    public void testSerialization(){
        String record = "{\"product_id\": \"T1\", \"description\": \"Product 1\", \"target_temperature\": 0.04875617145514566,\"target_humidity_level\": 0.4, \"content_type\": 1 }";
        Jsonb parser = JsonbBuilder.create();
        Product p = parser.fromJson(record,Product.class);
        assertEquals("T1", p.product_id);
        assertEquals("Product 1", p.description);
        assertEquals(0.4, p.target_humidity_level);
    }
}
