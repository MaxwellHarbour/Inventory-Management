package inventorymanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import inventorymanagement.demo.Transactions.Transaction;
import inventorymanagement.demo.Transactions.TransactionDTO;
import inventorymanagement.demo.Transactions.TransactionProducer;
import inventorymanagement.demo.Transactions.TransactionRepository;
import inventorymanagement.demo.Transactions.TransactionService;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Mock
    private TransactionProducer transactionProducer;

    @Mock
    private TransactionRepository transactionRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionProducer, transactionRepository);
    }

    @Test
    void createTransactionUsesCurrentDateWhenTimestampMissing() {
        TransactionDTO input = new TransactionDTO(42, 5, "receive", "");

        TransactionDTO result = transactionService.createTransaction(input);

        assertEquals(42, result.sku());
        assertEquals(5, result.quantity());
        assertEquals("receive", result.transactionType());
        assertEquals(LocalDate.now().format(DATE_FORMATTER), result.timestamp());
        verify(transactionProducer).sendTransaction(argThat(transaction ->
                transaction.getProductSku() == 42
                        && transaction.getQuantity() == 5
                        && "receive".equals(transaction.getTransactionType())
                        && transaction.getTransactionDate().equals(LocalDate.now().format(DATE_FORMATTER))));
    }

    @Test
    void createTransactionRejectsUnknownTransactionType() {
        TransactionDTO input = new TransactionDTO(42, 5, "unknown", "01/01/2024");

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(input));
    }

    @Test
    void getTransactionsLastSevenDaysFiltersToRecentEntries() {
        LocalDate today = LocalDate.now();
        when(transactionRepository.findAll()).thenReturn(List.of(
                new Transaction(null, 1, 5, "receive", today.format(DATE_FORMATTER)),
                new Transaction(null, 2, 10, "sell", today.minusDays(6).format(DATE_FORMATTER)),
                new Transaction(null, 3, 2, "return", today.minusDays(8).format(DATE_FORMATTER))
        ));

        List<TransactionDTO> result = transactionService.getTransactionsLastSevenDays();

        assertEquals(2, result.size());
        assertEquals(List.of(1, 2), result.stream().map(TransactionDTO::sku).toList());
    }

    @Test
    void getNegativeTransactionsReturnsOnlyNegativeEntries() {
        when(transactionRepository.findAll()).thenReturn(List.of(
                new Transaction(null, 1, 5, "receive", "01/01/2024"),
                new Transaction(null, 2, -3, "damage", "01/02/2024"),
                new Transaction(null, 3, -1, "sell", "01/03/2024")
        ));

        List<TransactionDTO> result = transactionService.getNegativeTransactions();

        assertEquals(2, result.size());
        assertEquals(List.of(2, 3), result.stream().map(TransactionDTO::sku).toList());
    }
}
