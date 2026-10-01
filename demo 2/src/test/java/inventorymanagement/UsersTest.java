package inventorymanagement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import inventorymanagement.demo.Users.Users;

class UsersTest {

    @Test
    void authoritiesNormalizeRoleWithRolePrefix() {
        Users user = new Users("alice", "encoded-password", "manager");

        assertEquals(java.util.List.of(new SimpleGrantedAuthority("ROLE_MANAGER")),
                user.getAuthorities().stream().toList());
    }

    @Test
    void authoritiesFallBackToUserRoleWhenRoleIsNull() {
        Users user = new Users("alice", "encoded-password", null);

        assertEquals(java.util.List.of(new SimpleGrantedAuthority("ROLE_USER")),
                user.getAuthorities().stream().toList());
    }
}
