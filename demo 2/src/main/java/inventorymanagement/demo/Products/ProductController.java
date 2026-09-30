package inventorymanagement.demo.Products;

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

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.findAllProducts();
    }

    @GetMapping("/{sku}")
    public ProductDTO getProductBySku(@PathVariable int sku) {
        return productService.getProductBySku(sku);
    }

    // Everything below this line should only be used by seller users
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        return productService.saveProduct(productDTO);
    }

    @PutMapping("/{sku}")
    public ProductDTO updateProduct(@PathVariable int sku, @RequestBody ProductDTO updatedProduct) {
        return productService.updateProduct(sku, updatedProduct);
    }

    @DeleteMapping("/{sku}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable int sku) {
        productService.deleteProduct(sku);
    }

    @PutMapping("/update-on-hand/{sku}")
    public ProductDTO updateOnHand(@PathVariable int sku, @RequestParam(value = "newOnHand") int newOnHand) {
        return productService.updateOnHand(sku, newOnHand);
    }


}
