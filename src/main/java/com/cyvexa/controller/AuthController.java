package com.cyvexa.controller;

import com.cyvexa.model.User;
import com.cyvexa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // Predefined Admin Check
        if ("admin@cyvexa.com".equals(loginRequest.getEmail()) && "Admin@123".equals(loginRequest.getPassword())) {
            User admin = new User();
            admin.setEmail("admin@cyvexa.com");
            admin.setFullName("System Admin");
            admin.setRole("ADMIN");
            return ResponseEntity.ok(admin);
        }

        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(loginRequest.getPassword())) {
            User user = userOpt.get();
            // Ensure default role if missing
            if (user.getRole() == null) user.setRole("STUDENT");
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        return ResponseEntity.ok(userRepository.save(user));
    }
}
