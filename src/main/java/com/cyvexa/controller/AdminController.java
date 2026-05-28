package com.cyvexa.controller;

import com.cyvexa.service.SessionService;
import com.cyvexa.repository.UserRepository;
import com.cyvexa.repository.CourseRepository;
import com.cyvexa.repository.EnrollmentRepository;
import com.cyvexa.repository.LessonRepository;
import com.cyvexa.repository.LessonProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private SessionService sessionService;

    @PostMapping("/cleanup")
    public ResponseEntity<?> cleanupDatabase() {
        try {
            lessonProgressRepository.deleteAll();
            enrollmentRepository.deleteAll();
            lessonRepository.deleteAll();
            courseRepository.deleteAll();
            userRepository.findAll().forEach(user -> {
                if (!"admin@cyvexa.com".equals(user.getEmail())) {
                    userRepository.delete(user);
                }
            });
            sessionService.clearAllSessions();
            return ResponseEntity.ok(Map.of("message", "Database cleaned successfully. Only admin@cyvexa.com retained."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Cleanup failed: " + e.getMessage()));
        }
    }
}
