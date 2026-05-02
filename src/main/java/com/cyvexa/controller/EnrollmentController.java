package com.cyvexa.controller;

import com.cyvexa.model.Course;
import com.cyvexa.model.Enrollment;
import com.cyvexa.model.User;
import com.cyvexa.repository.CourseRepository;
import com.cyvexa.repository.EnrollmentRepository;
import com.cyvexa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping
    public ResponseEntity<?> enroll(@RequestBody Map<String, Long> payload) {
        Long userId = payload.get("userId");
        Long courseId = payload.get("courseId");

        User user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);

        if (user == null || course == null) {
            return ResponseEntity.badRequest().body("User or Course not found");
        }

        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            return ResponseEntity.ok("Already enrolled");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(user);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setProgress(0);

        return ResponseEntity.ok(enrollmentRepository.save(enrollment));
    }

    @GetMapping("/user/{userId}")
    public List<Enrollment> getUserEnrollments(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();
        return enrollmentRepository.findByUser(user);
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkEnrollment(@RequestParam Long userId, @RequestParam Long courseId) {
        User user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (user == null || course == null) return ResponseEntity.ok(false);
        return ResponseEntity.ok(enrollmentRepository.existsByUserAndCourse(user, course));
    }
}
