package ibm.eda.demo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.schema.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.stream.schema.client.EnableSchemaRegistryClient;
import org.springframework.cloud.stream.schema.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;

@SpringBootApplication(scanBasePackages = { "ibm.eda.demo.*" })
//@EnableSchemaRegistryClient
public class OrderServiceApplication {

	@Value("${spring.cloud.stream.kafka.binder.producer-properties.schema.registry.url}")
	private String schemaRegistryURL;

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	public SchemaRegistryClient schemaRegistryClient() {
		ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
		client.setEndpoint(schemaRegistryURL);
		return client;
	}
}
