package inventorymanagement.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    private int sku;

    private String name;
    private double price;
    private int onHand;

    public Product() {
    }

    public Product(String name, double price, int sku, int onHand) {
        this.name = name;
        this.price = price;
        this.sku = sku;
        this.onHand = onHand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSku() {
        return sku;
    }

    public void setSku(int sku) {
        this.sku = sku;
    }

    public int getOnHand() {
        return onHand;
    }

    public void setOnHand(int onHand) {
        this.onHand = onHand;
    }
}
