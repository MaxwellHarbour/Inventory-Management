package inventorymanagement.demo;

import org.springframework.stereotype.Service;


@Service 
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductBySku(int sku) {
        return productRepository.findById(sku).orElse(null);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(int sku, Product updatedProduct) {
        Product existing = productRepository.findById(sku)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setName(updatedProduct.getName());
        existing.setPrice(updatedProduct.getPrice());
        existing.setOnHand(updatedProduct.getOnHand());
        existing.setSku(updatedProduct.getSku());

        return productRepository.save(existing);
    }

    public void deleteProduct(int sku) {
        productRepository.deleteById(sku);
    }

    public Product updateOnHand(int sku, int newOnHand) {
        Product existing = productRepository.findById(sku)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        existing.setOnHand(newOnHand);
        return productRepository.save(existing);
    }

}
