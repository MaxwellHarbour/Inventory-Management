package inventorymanagement.demo.Transactions;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import inventorymanagement.demo.Products.Product;
import inventorymanagement.demo.Products.ProductRepository;
import jakarta.transaction.Transactional;

@Service 
public class TransactionConsumer {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    public TransactionConsumer(TransactionRepository transactionRepository, ProductRepository productRepository) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    @KafkaListener (topics = "transactions", groupId = "transaction-group")
    @Transactional 
    public void listen(Transaction transaction) {
        Integer transactionId = transaction.getTransactionId();
        if (transactionId != null && transactionRepository.existsById(transactionId)) {
            return;
        }

        Product product = productRepository.findById(transaction.getProductSku()).orElseThrow(() -> new RuntimeException("Product not found"));
        int newOnHand = product.getOnHand() + transaction.getQuantity();

        product.setOnHand(newOnHand);
        productRepository.save(product);

        transactionRepository.save(transaction);
    }

    

}
