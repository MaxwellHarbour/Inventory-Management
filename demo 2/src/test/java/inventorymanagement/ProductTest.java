package inventorymanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import inventorymanagement.demo.Products.Product;
import inventorymanagement.demo.Products.ProductDTO;
import inventorymanagement.demo.Products.ProductRepository;
import inventorymanagement.demo.Products.ProductService;

@ExtendWith(MockitoExtension.class)
class ProductTest {

	@Mock
	private ProductRepository productRepository;

	private ProductService productService;

	@BeforeEach
	void setUp() {
		productService = new ProductService(productRepository);
	}

	@Test
	void getProductBySkuReturnsProductWhenPresent() {
		Product product = product(248234);
		when(productRepository.findById(248234)).thenReturn(Optional.of(product));

		assertEquals(new ProductDTO(248234, "Product", 19.99, 20), productService.getProductBySku(248234));
	}

	@Test
	void getProductBySkuThrowsNotFoundWhenMissing() {
		when(productRepository.findById(248234)).thenReturn(Optional.empty());

		assertThrows(java.util.NoSuchElementException.class, () -> productService.getProductBySku(248234));
	}

	@Test
	void saveProductDelegatesToRepository() {
		ProductDTO input = new ProductDTO(248234, "Product", 19.99, 20);
		Product savedProduct = product(248234);
		when(productRepository.save(org.mockito.ArgumentMatchers.any(Product.class))).thenReturn(savedProduct);

		assertEquals(new ProductDTO(248234, "Product", 19.99, 20), productService.saveProduct(input));
		verify(productRepository).save(org.mockito.ArgumentMatchers.argThat(product ->
				product.getSku() == input.sku()
						&& product.getName().equals(input.name())
						&& product.getPrice() == input.price()
						&& product.getOnHand() == input.onHand()));
	}

	@Test
	void updateProductUpdatesExistingProduct() {
		Product existing = product(248234);
		ProductDTO updated = new ProductDTO(248235, "Updated", 29.99, 40);
		when(productRepository.findById(248234)).thenReturn(Optional.of(existing));
		when(productRepository.save(existing)).thenReturn(existing);

		ProductDTO result = productService.updateProduct(248234, updated);

		assertEquals(new ProductDTO(248235, "Updated", 29.99, 40), result);
		assertEquals("Updated", existing.getName());
		assertEquals(29.99, existing.getPrice());
		assertEquals(248235, existing.getSku());
		assertEquals(40, existing.getOnHand());
		verify(productRepository).save(existing);
	}

	@Test
	void updateProductThrowsWhenProductIsMissing() {
		when(productRepository.findById(248234)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class,
				() -> productService.updateProduct(248234, new ProductDTO(248235, "Product", 19.99, 20)));
	}

	@Test
	void deleteProductDelegatesToRepository() {
		productService.deleteProduct(248234);

		verify(productRepository).deleteById(248234);
	}

	@Test
	void updateOnHandUpdatesExistingProduct() {
		Product existing = product(248234);
		when(productRepository.findById(248234)).thenReturn(Optional.of(existing));
		when(productRepository.save(existing)).thenReturn(existing);

		ProductDTO result = productService.updateOnHand(248234, 55);

		assertEquals(new ProductDTO(248234, "Product", 19.99, 55), result);
		assertEquals(55, existing.getOnHand());
		verify(productRepository).save(existing);
	}

	@Test
	void updateOnHandThrowsWhenProductIsMissing() {
		when(productRepository.findById(248234)).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class,
				() -> productService.updateOnHand(248234, 55));
	}

	private Product product(int sku) {
		return new Product("Product", 19.99, sku, 20);
	}
}
