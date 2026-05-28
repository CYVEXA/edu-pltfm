package com.cyvexa.config;

import com.cyvexa.service.SessionService;
import com.cyvexa.repository.UserRepository;
import com.cyvexa.repository.CourseRepository;
import com.cyvexa.repository.EnrollmentRepository;
import com.cyvexa.repository.LessonRepository;
import com.cyvexa.repository.LessonProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanupRunner implements CommandLineRunner {

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

    @Override
    public void run(String... args) throws Exception {
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
        System.out.println("Database cleanup complete. All data removed except admin@cyvexa.com.");
    }
}
