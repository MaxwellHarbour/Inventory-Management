package inventorymanagement;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import inventorymanagement.demo.Products.ProductController;
import inventorymanagement.demo.Products.ProductDTO;
import inventorymanagement.demo.Products.ProductService;
import inventorymanagement.demo.ErrorHandler;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductController(productService))
            .setControllerAdvice(new ErrorHandler())
                .build();
    }

    @Test
    void getAllProductsReturnsProducts() throws Exception {
        when(productService.findAllProducts()).thenReturn(List.of(productDTO(248234)));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"name\":\"Product\",\"price\":19.99,\"sku\":248234,\"onHand\":20}]"));

        verify(productService).findAllProducts();
    }

    @Test
    void getProductBySkuReturnsProduct() throws Exception {
        when(productService.getProductBySku(248234)).thenReturn(productDTO(248234));

        mockMvc.perform(get("/products/248234"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"name\":\"Product\",\"price\":19.99,\"sku\":248234,\"onHand\":20}"));

        verify(productService).getProductBySku(248234);
    }

    @Test
    void getMissingProductUsesGlobalNotFoundResponse() throws Exception {
        when(productService.getProductBySku(248234))
                .thenThrow(new java.util.NoSuchElementException("Product not found"));

        mockMvc.perform(get("/products/248234"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Product not found\"}"));
    }

    @Test
    void createProductUsesService() throws Exception {
        when(productService.saveProduct(org.mockito.ArgumentMatchers.any(ProductDTO.class))).thenReturn(productDTO(248234));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Product\",\"price\":19.99,\"sku\":248234,\"onHand\":20}"))
                .andExpect(status().isCreated())
                .andExpect(content().json("{\"name\":\"Product\",\"price\":19.99,\"sku\":248234,\"onHand\":20}"));

        verify(productService).saveProduct(org.mockito.ArgumentMatchers.any(ProductDTO.class));
    }

    @Test
    void updateProductUsesService() throws Exception {
        when(productService.updateProduct(org.mockito.ArgumentMatchers.eq(248234), org.mockito.ArgumentMatchers.any(ProductDTO.class)))
            .thenReturn(productDTO(248235));

        mockMvc.perform(put("/products/248234")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Updated\",\"price\":29.99,\"sku\":248235,\"onHand\":40}"))
                .andExpect(status().isOk());

        verify(productService).updateProduct(org.mockito.ArgumentMatchers.eq(248234),
            org.mockito.ArgumentMatchers.any(ProductDTO.class));
    }

    @Test
    void deleteProductUsesService() throws Exception {
        mockMvc.perform(delete("/products/248234"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(248234);
    }

    @Test
    void updateOnHandUsesService() throws Exception {
        when(productService.updateOnHand(248234, 55)).thenReturn(productDTO(248234));

        mockMvc.perform(put("/products/update-on-hand/248234")
                .param("newOnHand", "55"))
                .andExpect(status().isOk());

        verify(productService).updateOnHand(248234, 55);
    }

    private ProductDTO productDTO(int sku) {
        return new ProductDTO(sku, "Product", 19.99, 20);
    }
}