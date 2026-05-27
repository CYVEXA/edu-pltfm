package com.cyvexa.controller;

import com.cyvexa.model.User;
import com.cyvexa.repository.UserRepository;
import com.cyvexa.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        // Predefined Admin Check
        if ("admin@cyvexa.com".equals(loginRequest.getEmail()) && "Admin@123".equals(loginRequest.getPassword())) {
            User admin = new User();
            admin.setEmail("admin@cyvexa.com");
            admin.setFullName("System Admin");
            admin.setRole("ADMIN");
            String token = sessionService.createSession(admin);
            return ResponseEntity.ok(Map.of("user", admin, "token", token));
        }

        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(loginRequest.getPassword())) {
            User user = userOpt.get();
            // Ensure default role if missing
            if (user.getRole() == null) user.setRole("STUDENT");
            String token = sessionService.createSession(user);
            return ResponseEntity.ok(Map.of("user", user, "token", token));
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token != null && sessionService.isValid(token)) {
            return ResponseEntity.ok(Map.of("valid", true, "user", sessionService.getUser(token)));
        }
        return ResponseEntity.status(401).body(Map.of("valid", false));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token != null) {
            sessionService.removeSession(token);
        }
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        return ResponseEntity.ok(userRepository.save(user));
    }
}
