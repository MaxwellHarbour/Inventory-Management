package inventorymanagement.demo.Transactions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final TransactionProducer transactionProducer;
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionProducer transactionProducer, TransactionRepository transactionRepository) {
        this.transactionProducer = transactionProducer;
        this.transactionRepository = transactionRepository;
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = toTransaction(transactionDTO);
        transactionProducer.sendTransaction(transaction);
        return toTransactionDTO(transaction);
    }

    public List<TransactionDTO> getTransactionsLastSevenDays() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        return transactionRepository.findAll().stream()
                .map(this::toTransactionDTO)
                .filter(transaction -> {
                    LocalDate transactionDate = LocalDate.parse(transaction.timestamp(), DATE_FORMATTER);
                    return !transactionDate.isBefore(sevenDaysAgo) && !transactionDate.isAfter(today);
                })
                .toList();
    }

    public TransactionDTO getTransactionBySku(int productSku) {
        // Already returns a TransactionDTO object, so no need to convert it again
        TransactionDTO transaction = transactionRepository.findByProductSku(productSku);
        if (transaction == null) {
            throw new java.util.NoSuchElementException("Transaction not found");
        }
        return transaction;
    }   

    public List<TransactionDTO> getNegativeTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::toTransactionDTO)
                .filter(transaction -> transaction.quantity() < 0)
                .toList();
    }

    private Transaction toTransaction(TransactionDTO transactionDTO) {
        String transactionDate = transactionDTO.timestamp() == null || transactionDTO.timestamp().isBlank()
                ? LocalDate.now().format(DATE_FORMATTER)
                : transactionDTO.timestamp();

        List<String> validTransactionTypes = List.of("receive", "sell", "return", "damage", "manual-adjustment");

        if (!validTransactionTypes.contains(transactionDTO.transactionType())) {
            throw new IllegalArgumentException("Invalid transaction type: " + transactionDTO.transactionType());
        }

        return new Transaction(
                null,
                transactionDTO.sku(),
                transactionDTO.quantity(),
                transactionDTO.transactionType(),
                transactionDate
        );
    }

    private TransactionDTO toTransactionDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getProductSku(),
                transaction.getQuantity(),
                transaction.getTransactionType(),
                transaction.getTransactionDate()
        );
    }
}

