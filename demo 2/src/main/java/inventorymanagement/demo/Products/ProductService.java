package inventorymanagement.demo.Products;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;


@Service 
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDTO getProductBySku(int sku) {
        return productRepository.findById(sku)
                .map(this::toProductDTO)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
    }

    public ProductDTO saveProduct(ProductDTO productDTO) {
        Product product = toProduct(productDTO);
        return toProductDTO(productRepository.save(product));
    }

    public ProductDTO updateProduct(int sku, ProductDTO updatedProduct) {
        Product existing = productRepository.findById(sku)
            .orElseThrow(() -> new NoSuchElementException("Product not found"));

        existing.setName(updatedProduct.name());
        existing.setPrice(updatedProduct.price());
        existing.setOnHand(updatedProduct.onHand());
        existing.setSku(updatedProduct.sku());

        return toProductDTO(productRepository.save(existing));
    }

    public void deleteProduct(int sku) {
        productRepository.deleteById(sku);
    }

    public ProductDTO updateOnHand(int sku, int newOnHand) {
        Product existing = productRepository.findById(sku)
            .orElseThrow(() -> new NoSuchElementException("Product not found"));

        existing.setOnHand(newOnHand);
        return toProductDTO(productRepository.save(existing));
    }

    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toProductDTO)
                .toList();
    }

    private ProductDTO toProductDTO(Product product) {
        return new ProductDTO(product.getSku(), product.getName(), product.getPrice(), product.getOnHand());
    }

    private Product toProduct(ProductDTO productDTO) {
        return new Product(productDTO.name(), productDTO.price(), productDTO.sku(), productDTO.onHand());
    }

}
