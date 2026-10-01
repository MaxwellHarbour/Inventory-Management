package inventorymanagement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import inventorymanagement.demo.Users.UsersController;
import inventorymanagement.demo.Users.UsersDTO;
import inventorymanagement.demo.Users.UsersService;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

    @Mock
    private UsersService usersService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UsersController(usersService)).build();
    }

    @Test
    void loginAcceptsUsernameAndPasswordDto() throws Exception {
        when(usersService.login(any(UsersDTO.class))).thenReturn(ResponseEntity.ok("jwt-token"));

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));

        verify(usersService).login(new UsersDTO("alice", "secret"));
    }

    @Test
    void registerUsesCurrentApiPathAndDto() throws Exception {
        when(usersService.register(any(UsersDTO.class)))
                .thenReturn(ResponseEntity.ok("User registered successfully"));

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"alice\",\"password\":\"secret\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));

        verify(usersService).register(new UsersDTO("alice", "secret"));
    }

    @Test
    void updateRoleUsesTheCurrentRoute() throws Exception {
        when(usersService.updateRole("alice", "MANAGER"))
                .thenReturn(ResponseEntity.ok("User role updated successfully"));

        mockMvc.perform(get("/api/users/update-role/alice/MANAGER"))
                .andExpect(status().isOk())
                .andExpect(content().string("User role updated successfully"));

        verify(usersService).updateRole("alice", "MANAGER");
    }
}
