package inventorymanagement.demo.Transactions;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table(name = "transactions")
public class Transaction {

    @Id
    private String transactionId;
    private  int productSku;
    private  int quantity;
    private  String transactionType; // purchase, sale, return, manual_adjustment, markdown, etc.
    private  String transactionDate; // Format: MM/DD/YYYY

    public Transaction() {}

    public Transaction(String transactionId, int productSku, int quantity, String transactionType, String transactionDate) {
        this.transactionId = transactionId;
        this.productSku = productSku;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
    }

    public String getTransactionId() { return transactionId; }
    public int getProductSku() { return productSku; }
    public int getQuantity() { return quantity; }
    public String getTransactionType() { return transactionType; }
    public String getTransactionDate() { return transactionDate; }

    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setProductSku(int productSku) { this.productSku = productSku; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

}
