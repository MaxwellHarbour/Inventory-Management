package inventorymanagement.demo.Users;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UsersDTO usersDTO) {
        return usersService.login(usersDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UsersDTO usersDTO) {
        return usersService.register(usersDTO);
    }

    @GetMapping ("/update-role/{username}/{newrole}")
    @PreAuthorize ("hasRole('ADMIN')")
    public ResponseEntity<String> updateRole(@PathVariable String username, @PathVariable String newrole) {
        return usersService.updateRole(username, newrole);
    }

}
