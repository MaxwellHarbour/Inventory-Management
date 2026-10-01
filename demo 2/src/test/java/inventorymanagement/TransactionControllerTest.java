package inventorymanagement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import inventorymanagement.demo.Transactions.TransactionController;
import inventorymanagement.demo.Transactions.TransactionDTO;
import inventorymanagement.demo.Transactions.TransactionService;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TransactionController(transactionService))
                .build();
    }

    @Test
    void receiveInventoryUsesPathSkuAndReceiveType() throws Exception {
        TransactionDTO expected = new TransactionDTO(42, 5, "receive", "01/01/2024");
        when(transactionService.createTransaction(expected)).thenReturn(expected);

        mockMvc.perform(post("/transactions/inventory/42/receive")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":999,\"quantity\":5,\"transactionType\":\"ignored\",\"timestamp\":\"01/01/2024\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sku\":42,\"quantity\":5,\"transactionType\":\"receive\",\"timestamp\":\"01/01/2024\"}"));

        verify(transactionService).createTransaction(expected);
    }

    @Test
    void sellInventoryConvertsQuantityToNegative() throws Exception {
        TransactionDTO response = new TransactionDTO(42, -3, "sell", "01/01/2024");
        when(transactionService.createTransaction(any(TransactionDTO.class))).thenReturn(response);

        mockMvc.perform(post("/transactions/inventory/42/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":42,\"quantity\":3,\"transactionType\":\"sell\",\"timestamp\":\"01/01/2024\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sku\":42,\"quantity\":-3,\"transactionType\":\"sell\",\"timestamp\":\"01/01/2024\"}"));

        verify(transactionService).createTransaction(eq(new TransactionDTO(42, -3, "sell", "01/01/2024")));
    }

    @Test
    void returnInventoryUsesReturnTypeAndKeepsPositiveQuantity() throws Exception {
        TransactionDTO expected = new TransactionDTO(42, 3, "return", "01/01/2024");
        when(transactionService.createTransaction(expected)).thenReturn(expected);

        mockMvc.perform(post("/transactions/inventory/42/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":999,\"quantity\":3,\"transactionType\":\"ignored\",\"timestamp\":\"01/01/2024\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sku\":42,\"quantity\":3,\"transactionType\":\"return\",\"timestamp\":\"01/01/2024\"}"));

        verify(transactionService).createTransaction(expected);
    }

    @Test
    void reportDamageConvertsQuantityToNegative() throws Exception {
        TransactionDTO expected = new TransactionDTO(42, -3, "damage", "01/01/2024");
        when(transactionService.createTransaction(expected)).thenReturn(expected);

        mockMvc.perform(post("/transactions/inventory/42/damage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":999,\"quantity\":3,\"transactionType\":\"ignored\",\"timestamp\":\"01/01/2024\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sku\":42,\"quantity\":-3,\"transactionType\":\"damage\",\"timestamp\":\"01/01/2024\"}"));

        verify(transactionService).createTransaction(expected);
    }

    @Test
    void adjustInventoryUsesManualAdjustmentType() throws Exception {
        TransactionDTO expected = new TransactionDTO(42, 3, "manual-adjustment", "01/01/2024");
        when(transactionService.createTransaction(expected)).thenReturn(expected);

        mockMvc.perform(post("/transactions/inventory/42/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sku\":999,\"quantity\":3,\"transactionType\":\"ignored\",\"timestamp\":\"01/01/2024\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sku\":42,\"quantity\":3,\"transactionType\":\"manual-adjustment\",\"timestamp\":\"01/01/2024\"}"));

        verify(transactionService).createTransaction(expected);
    }

    @Test
    void getTransactionsHistoryUsesService() throws Exception {
        when(transactionService.getTransactionsLastSevenDays()).thenReturn(List.of(
                new TransactionDTO(42, 5, "receive", "01/01/2024")));

        mockMvc.perform(get("/transactions/history"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"sku\":42,\"quantity\":5,\"transactionType\":\"receive\",\"timestamp\":\"01/01/2024\"}]"));

        verify(transactionService).getTransactionsLastSevenDays();
    }

    @Test
    void getTransactionHistoryBySkuUsesService() throws Exception {
        TransactionDTO transaction = new TransactionDTO(42, -3, "sell", "01/01/2024");
        when(transactionService.getTransactionBySku(42)).thenReturn(transaction);

        mockMvc.perform(get("/transactions/history/42"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sku\":42,\"quantity\":-3,\"transactionType\":\"sell\",\"timestamp\":\"01/01/2024\"}"));

        verify(transactionService).getTransactionBySku(42);
    }

    @Test
    void getNegativeHistoryUsesService() throws Exception {
        when(transactionService.getNegativeTransactions()).thenReturn(List.of(
                new TransactionDTO(42, -3, "sell", "01/01/2024")));

        mockMvc.perform(get("/transactions/negative-history"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"sku\":42,\"quantity\":-3,\"transactionType\":\"sell\",\"timestamp\":\"01/01/2024\"}]"));

        verify(transactionService).getNegativeTransactions();
    }
}
