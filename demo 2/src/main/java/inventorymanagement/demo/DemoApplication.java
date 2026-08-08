package inventorymanagement.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class DemoApplication {

	private final ProductRepository productRepository;

	public DemoApplication(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void insertSampleProduct() {
		if (productRepository.count() == 0) {
			productRepository.save(new Product("Sample Product", 19.99, 248234, 20));
			productRepository.save(new Product("Sample Product2", 29.99, 248235, 15));
			productRepository.save(new Product("Sample Product3", 39.99, 248236, 25));
		}
	}

}
