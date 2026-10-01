package inventorymanagement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;

import inventorymanagement.demo.Products.ProductController;
import inventorymanagement.demo.Products.ProductDTO;
import inventorymanagement.demo.Products.ProductService;
import inventorymanagement.demo.Users.UsersController;
import inventorymanagement.demo.Users.UsersService;

class MethodAuthorizationTest {

    private AnnotationConfigApplicationContext context;
    private ProductService productService;
    private UsersService usersService;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(TestSecurityConfiguration.class);
        productService = context.getBean(ProductService.class);
        usersService = context.getBean(UsersService.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        context.close();
    }

    @Test
    void managerCanUpdateProduct() {
        authenticateAs("ROLE_MANAGER");

        context.getBean(ProductController.class).updateProduct(
                42, new ProductDTO(42, "Item", 2.50, 10));

        verify(productService).updateProduct(42, new ProductDTO(42, "Item", 2.50, 10));
    }

    @Test
    void associateCannotDeleteProduct() {
        authenticateAs("ROLE_ASSOCIATE");

        assertThrows(AccessDeniedException.class,
                () -> context.getBean(ProductController.class).deleteProduct(42));
    }

    @Test
    void adminCanUpdateUserRole() {
        authenticateAs("ROLE_ADMIN");

        context.getBean(UsersController.class).updateRole("alice", "MANAGER");

        verify(usersService).updateRole("alice", "MANAGER");
    }

    @Test
    void managerCannotUpdateUserRole() {
        authenticateAs("ROLE_MANAGER");

        assertThrows(AccessDeniedException.class,
                () -> context.getBean(UsersController.class).updateRole("alice", "MANAGER"));
    }

    private void authenticateAs(String role) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test-user", "", List.of(new SimpleGrantedAuthority(role))));
    }

    @Configuration
    @EnableMethodSecurity
    static class TestSecurityConfiguration {
        @Bean
        ProductService productService() {
            return mock(ProductService.class);
        }

        @Bean
        UsersService usersService() {
            UsersService service = mock(UsersService.class);
            org.mockito.Mockito.when(service.updateRole("alice", "MANAGER"))
                    .thenReturn(ResponseEntity.ok("User role updated successfully"));
            return service;
        }

        @Bean
        ProductController productController(ProductService productService) {
            return new ProductController(productService);
        }

        @Bean
        UsersController usersController(UsersService usersService) {
            return new UsersController(usersService);
        }
    }
}
