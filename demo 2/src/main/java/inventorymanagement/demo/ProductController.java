package inventorymanagement.demo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductService productService;

    public ProductController(ProductRepository productRepository, ProductService productService) {
        this.productRepository = productRepository;
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{sku}")
    public Product getProductBySku(@PathVariable int sku) {
        return productRepository.findById(sku).orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @PutMapping("/{sku}")
    public Product updateProduct(@PathVariable int sku, @RequestBody Product updatedProduct) {
        return productService.updateProduct(sku, updatedProduct);
    }

    @DeleteMapping("/{sku}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable int sku) {
        productService.deleteProduct(sku);
    }

    @PutMapping("/update-on-hand/{sku}")
    public Product updateOnHand(@PathVariable int sku, @RequestParam(value = "newOnHand") int newOnHand) {
        return productService.updateOnHand(sku, newOnHand);
    }


}
