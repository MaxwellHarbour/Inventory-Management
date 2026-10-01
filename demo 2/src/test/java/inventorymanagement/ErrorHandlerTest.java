package inventorymanagement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import inventorymanagement.demo.ErrorHandler;

class ErrorHandlerTest {

    @Test
    void runtimeExceptionReturnsJsonErrorResponse() throws Exception {
        mockMvc().perform(get("/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"An unexpected error occurred\"}"));
    }

    @Test
    void illegalArgumentReturnsBadRequest() throws Exception {
        mockMvc().perform(get("/bad-request"))
            .andExpect(status().isBadRequest())
            .andExpect(content().json("{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid input\"}"));
    }

    @Test
    void missingElementReturnsNotFound() throws Exception {
        mockMvc().perform(get("/missing"))
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"status\":404,\"error\":\"Not Found\",\"message\":\"Missing item\"}"));
    }

    @Test
    void authenticationFailureUsesUnauthorizedJsonResponse() throws ServletException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new ErrorHandler().commence(new MockHttpServletRequest(), response,
                new BadCredentialsException("bad credentials"));

        org.junit.jupiter.api.Assertions.assertEquals(401, response.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("application/json", response.getContentType());
        org.junit.jupiter.api.Assertions.assertTrue(response.getContentAsString().contains("Authentication required"));
    }

    @Test
    void accessDeniedUsesForbiddenJsonResponse() throws ServletException, IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new ErrorHandler().handle(new MockHttpServletRequest(), response,
                new AccessDeniedException("forbidden"));

        org.junit.jupiter.api.Assertions.assertEquals(403, response.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals("application/json", response.getContentType());
        org.junit.jupiter.api.Assertions.assertTrue(response.getContentAsString().contains("Access denied"));
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders
            .standaloneSetup(new TestController())
            .setControllerAdvice(new ErrorHandler())
            .build();
    }

    @RestController
    static class TestController {
        @GetMapping("/boom")
        public String boom() {
            throw new RuntimeException("Boom");
        }

        @GetMapping("/bad-request")
        public String badRequest() {
            throw new IllegalArgumentException("Invalid input");
        }

        @GetMapping("/missing")
        public String missing() {
            throw new java.util.NoSuchElementException("Missing item");
        }
    }
}
