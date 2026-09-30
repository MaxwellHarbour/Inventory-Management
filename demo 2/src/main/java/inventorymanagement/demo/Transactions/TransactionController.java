package inventorymanagement.demo.Transactions;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping ("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public TransactionDTO createTransaction(@RequestBody TransactionDTO transactionDTO) {
        return transactionService.createTransaction(transactionDTO);
    }

    @PostMapping ("/inventory/{sku}/receive")
    public TransactionDTO receiveInventory(@PathVariable int sku, @RequestBody TransactionDTO transactionDTO) {
        transactionDTO = new TransactionDTO(sku, transactionDTO.quantity(), "receive", transactionDTO.timestamp());
        return transactionService.createTransaction(transactionDTO);
    }

    @PostMapping ("/inventory/{sku}/sell")
    public TransactionDTO sellInventory(@PathVariable int sku, @RequestBody TransactionDTO transactionDTO) {
        transactionDTO = new TransactionDTO(sku, transactionDTO.quantity() * -1, "sell", transactionDTO.timestamp());
        return transactionService.createTransaction(transactionDTO);
    }

    @PostMapping ("/inventory/{sku}/return")
    public TransactionDTO returnInventory(@PathVariable int sku, @RequestBody TransactionDTO transactionDTO) {
        transactionDTO = new TransactionDTO(sku, transactionDTO.quantity(), "return", transactionDTO.timestamp());
        return transactionService.createTransaction(transactionDTO);
    }

    @PostMapping ("/inventory/{sku}/damage")
    public TransactionDTO reportDamage(@PathVariable int sku, @RequestBody TransactionDTO transactionDTO) {
        transactionDTO = new TransactionDTO(sku, transactionDTO.quantity() * -1, "damage", transactionDTO.timestamp());
        return transactionService.createTransaction(transactionDTO);
    }

    @PostMapping ("/inventory/{sku}/adjust")
    public TransactionDTO adjustInventory(@PathVariable int sku, @RequestBody TransactionDTO transactionDTO) {
        transactionDTO = new TransactionDTO(sku, transactionDTO.quantity(), "manual-adjustment", transactionDTO.timestamp());
        return transactionService.createTransaction(transactionDTO);
    }



    @GetMapping("/history/{sku}")
    public TransactionDTO getTransactionBySku(@PathVariable int sku) {
        return transactionService.getTransactionBySku(sku);
    }

    @GetMapping("/history")
    public List<TransactionDTO> getTransactions() {
        return transactionService.getTransactionsLastSevenDays();
    }

    @GetMapping("/negative-history")
    public List<TransactionDTO> getNegativeTransactions() {
        return transactionService.getNegativeTransactions();
    }

}
