package inventorymanagement;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import inventorymanagement.demo.ErrorHandler;

class ErrorHandlerTest {

    @Test
    void runtimeExceptionReturnsJsonErrorResponse() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new ErrorHandler())
                .build();

        mockMvc.perform(get("/boom"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"status\":500,\"error\":\"Internal Server Error\",\"message\":\"Boom\"}"));
    }

    @RestController
    static class TestController {
        @GetMapping("/boom")
        public String boom() {
            throw new RuntimeException("Boom");
        }
    }
}
