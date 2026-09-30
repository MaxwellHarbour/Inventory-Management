package inventorymanagement;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import inventorymanagement.demo.Transactions.Transaction;
import inventorymanagement.demo.Transactions.TransactionDTO;
import inventorymanagement.demo.Transactions.TransactionProducer;
import inventorymanagement.demo.Transactions.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionProducerTest {

    @Mock
    private KafkaTemplate<String, Transaction> kafkaTemplate;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void sendTransactionMapsDtoToEntityBeforePublishing() {
        TransactionProducer producer = new TransactionProducer(kafkaTemplate);
        TransactionDTO input = new TransactionDTO(42, 5, "purchase", "01/01/2023");

        producer.sendTransaction(new Transaction(
                null,
                input.sku(),
                input.quantity(),
                input.transactionType(),
                input.timestamp()));

        verify(kafkaTemplate).send(eq("transactions"), argThat(transaction ->
                transaction.getProductSku() == input.sku()
                        && transaction.getQuantity() == input.quantity()
                        && transaction.getTransactionType().equals(input.transactionType())
                        && transaction.getTransactionDate().equals(input.timestamp())
                        && transaction.getTransactionId() == null));
    }
}
