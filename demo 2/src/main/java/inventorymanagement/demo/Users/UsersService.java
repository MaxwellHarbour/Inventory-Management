package inventorymanagement.demo.Users;

import java.time.Instant;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    public ResponseEntity<String> login(UsersDTO usersDTO) {
        // Built in method findByUsername also acts as a DTO to Entity conversion
        Users user = usersRepository.findByUsername(usersDTO.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(usersDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = generateToken(user);
        return ResponseEntity.ok(token);
    }

    public ResponseEntity<String> register(UsersDTO usersDTO) {
        if (usersRepository.findByUsername(usersDTO.username()).isPresent()) {
            throw new IllegalStateException("User already exists");
        }

        String role = "ASSOCIATE"; // Default role for new users. Requires admin to update role to "Manager" or "Admin" if needed.
        String encryptedPassword = passwordEncoder.encode(usersDTO.password());
        Users newUser = new Users(usersDTO.username(), encryptedPassword, role);

        usersRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully");
    }

    public ResponseEntity<String> updateRole(String username, String newrole) {
        Users user = usersRepository.findByUsername(username)
            .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (newrole == null || newrole.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        if (!newrole.equalsIgnoreCase("ADMIN") && !newrole.equalsIgnoreCase("MANAGER") && !newrole.equalsIgnoreCase("ASSOCIATE")) {
            throw new IllegalArgumentException("Invalid role");
        }

        user.setRole(newrole);
        usersRepository.save(user);
        return ResponseEntity.ok("User role updated successfully");
    }

    private String generateToken(Users user) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(user.getUsername())
                .claim("role", user.getRole())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claims
        )).getTokenValue();
    }

}
