package inventorymanagement.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import inventorymanagement.demo.Products.Product;
import inventorymanagement.demo.Products.ProductRepository;
import inventorymanagement.demo.Transactions.TransactionRepository;

@SpringBootApplication
public class DemoApplication {

	private final ProductRepository productRepository;
	private final TransactionRepository transactionRepository;

	public DemoApplication(ProductRepository productRepository, TransactionRepository transactionRepository) {
		this.productRepository = productRepository;
		this.transactionRepository = transactionRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void insertSampleProduct() {
		productRepository.deleteAll();   // Clear existing products before inserting sample products
		transactionRepository.deleteAll(); // Clear existing transactions
		productRepository.save(new Product("Sample Product", 19.99, 248234, 20));
		productRepository.save(new Product("Sample Product2", 29.99, 248235, 15));
		productRepository.save(new Product("Sample Product3", 39.99, 248236, 25));
	}

}
