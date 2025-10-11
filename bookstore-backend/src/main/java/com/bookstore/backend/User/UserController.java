package com.bookstore.backend.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
}
