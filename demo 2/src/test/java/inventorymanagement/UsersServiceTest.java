package inventorymanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import inventorymanagement.demo.Users.Users;
import inventorymanagement.demo.Users.UsersDTO;
import inventorymanagement.demo.Users.UsersRepository;
import inventorymanagement.demo.Users.UsersService;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    private BCryptPasswordEncoder passwordEncoder;
    private UsersService usersService;

    @BeforeEach
    void setUp() {
        String secret = "test-secret-key-1234567890123456";
        JwtEncoder jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secret.getBytes(StandardCharsets.UTF_8)));
        passwordEncoder = new BCryptPasswordEncoder();
        usersService = new UsersService(usersRepository, passwordEncoder, jwtEncoder);
    }

    @Test
    void registerCreatesUserWithEncodedPassword() {
        UsersDTO input = new UsersDTO("alice", "password123");
        when(usersRepository.findByUsername("alice")).thenReturn(Optional.empty());

        ResponseEntity<String> response = usersService.register(input);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().contains("registered successfully"));

        ArgumentCaptor<Users> savedUser = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(savedUser.capture());
        assertEquals("alice", savedUser.getValue().getUsername());
        assertEquals("ASSOCIATE", savedUser.getValue().getRole());
        assertTrue(passwordEncoder.matches("password123", savedUser.getValue().getPassword()));
        }

        @Test
        void registerRejectsDuplicateUsername() {
        when(usersRepository.findByUsername("alice"))
            .thenReturn(Optional.of(new Users("alice", "encoded", "ASSOCIATE")));

        assertThrows(IllegalStateException.class,
            () -> usersService.register(new UsersDTO("alice", "password123")));
    }

    @Test
    void loginReturnsJwtForValidCredentials() {
        String encodedPassword = passwordEncoder.encode("password123");
        when(usersRepository.findByUsername("alice")).thenReturn(Optional.of(new Users("alice", encodedPassword, "admin")));

        ResponseEntity<String> response = usersService.login(new UsersDTO("alice", "password123"));

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().split("\\.").length >= 3);
    }

    @Test
    void loginRejectsUnknownUsername() {
        when(usersRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> usersService.login(new UsersDTO("missing", "password123")));
    }

    @Test
    void loginRejectsIncorrectPassword() {
        String encodedPassword = passwordEncoder.encode("correct-password");
        when(usersRepository.findByUsername("alice"))
                .thenReturn(Optional.of(new Users("alice", encodedPassword, "ASSOCIATE")));

        assertThrows(BadCredentialsException.class,
                () -> usersService.login(new UsersDTO("alice", "wrong-password")));
    }

    @Test
    void updateRolePersistsAllowedRole() {
        Users user = new Users("alice", "encoded", "ASSOCIATE");
        when(usersRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        ResponseEntity<String> response = usersService.updateRole("alice", "MANAGER");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("MANAGER", user.getRole());
        verify(usersRepository).save(user);
    }

    @Test
    void updateRoleRejectsUnsupportedRole() {
        Users user = new Users("alice", "encoded", "ASSOCIATE");
        when(usersRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class,
                () -> usersService.updateRole("alice", "OWNER"));
    }
}
