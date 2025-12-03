package com.bookstore.backend.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UserController {

    private final UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserController(UserRepository users) {
        this.users = users;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getEmail() == null || user.getPasswordHash() == null || user.getEmail().isBlank() || user.getPasswordHash().isBlank()) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }

        String email = user.getEmail().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            return ResponseEntity.status(409).body("Email already registered");
        }

        // Encode password
        String encodedPassword = encoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);
        user.setEmail(email);

        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }

        users.save(user);

        return ResponseEntity.ok("Account created for " + user.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null
                || request.getEmail().isBlank() || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email and password are required");
        }

        String email = request.getEmail().trim().toLowerCase();
        Optional<User> optionalUser = users.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        User user = optionalUser.get();

        if (!encoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole()
        );

        return ResponseEntity.ok(response);
    }
}
