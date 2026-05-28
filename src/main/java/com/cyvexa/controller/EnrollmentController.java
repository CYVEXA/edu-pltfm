package com.cyvexa.controller;

import com.cyvexa.model.Course;
import com.cyvexa.model.Enrollment;
import com.cyvexa.model.Lesson;
import com.cyvexa.model.LessonProgress;
import com.cyvexa.model.User;
import com.cyvexa.repository.CourseRepository;
import com.cyvexa.repository.EnrollmentRepository;
import com.cyvexa.repository.LessonProgressRepository;
import com.cyvexa.repository.LessonRepository;
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

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @PostMapping
    public ResponseEntity<?> enroll(@RequestParam Long userId, @RequestParam Long courseId) {

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

    @GetMapping("/course/{courseId}/stats")
    public ResponseEntity<?> getCourseStats(@PathVariable Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return ResponseEntity.notFound().build();
        long enrolled = enrollmentRepository.countByCourse(course);
        long completed = enrollmentRepository.countByCourseAndProgress(course, 100);
        return ResponseEntity.ok(Map.of("enrolledCount", enrolled, "completedCount", completed));
    }

    @GetMapping("/{enrollmentId}/progress")
    public ResponseEntity<?> getLessonProgress(@PathVariable Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElse(null);
        if (enrollment == null) return ResponseEntity.notFound().build();

        List<LessonProgress> progress = lessonProgressRepository.findByEnrollment(enrollment);
        long totalLessons = lessonRepository.findByCourse(enrollment.getCourse()).size();
        long completedLessons = lessonProgressRepository.countByEnrollmentAndCompletedTrue(enrollment);

        return ResponseEntity.ok(Map.of(
            "progress", progress,
            "completedLessons", completedLessons,
            "totalLessons", totalLessons
        ));
    }

    @PostMapping("/{enrollmentId}/lessons/{lessonId}/complete")
    public ResponseEntity<?> markLessonComplete(@PathVariable Long enrollmentId, @PathVariable Long lessonId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElse(null);
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);

        if (enrollment == null || lesson == null) {
            return ResponseEntity.badRequest().body("Enrollment or Lesson not found");
        }

        LessonProgress lp = lessonProgressRepository.findByEnrollmentAndLesson(enrollment, lesson)
            .orElse(new LessonProgress());

        if (!lp.isCompleted()) {
            lp.setEnrollment(enrollment);
            lp.setLesson(lesson);
            lp.setCompleted(true);
            lp.setCompletedAt(LocalDateTime.now());
            lessonProgressRepository.save(lp);

            // Recalculate overall progress
            long totalLessons = lessonRepository.findByCourse(enrollment.getCourse()).size();
            long completedLessons = lessonProgressRepository.countByEnrollmentAndCompletedTrue(enrollment);
            int newProgress = totalLessons > 0 ? (int) ((completedLessons * 100) / totalLessons) : 0;
            enrollment.setProgress(newProgress);
            enrollmentRepository.save(enrollment);
        }

        return ResponseEntity.ok(Map.of("message", "Lesson marked complete", "progress", enrollment.getProgress()));
    }

    @PostMapping("/{enrollmentId}/lessons/{lessonId}/progress")
    public ResponseEntity<?> updateLessonProgress(
            @PathVariable Long enrollmentId,
            @PathVariable Long lessonId,
            @RequestParam int percent) {
        
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElse(null);
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);

        if (enrollment == null || lesson == null) {
            return ResponseEntity.badRequest().body("Enrollment or Lesson not found");
        }

        LessonProgress lp = lessonProgressRepository.findByEnrollmentAndLesson(enrollment, lesson)
            .orElse(new LessonProgress());

        lp.setEnrollment(enrollment);
        lp.setLesson(lesson);
        
        if (percent > lp.getProgressPercent()) {
            lp.setProgressPercent(percent);
        }
        if (percent >= 100 && !lp.isCompleted()) {
            lp.setCompleted(true);
            lp.setCompletedAt(LocalDateTime.now());
        }
        lessonProgressRepository.save(lp);

        // Recalculate overall course progress
        List<Lesson> lessons = lessonRepository.findByCourse(enrollment.getCourse());
        int totalLessons = lessons.size();
        if (totalLessons > 0) {
            int totalProgressSum = 0;
            for (Lesson l : lessons) {
                LessonProgress prog = lessonProgressRepository.findByEnrollmentAndLesson(enrollment, l).orElse(null);
                if (prog != null) {
                    totalProgressSum += prog.isCompleted() ? 100 : prog.getProgressPercent();
                }
            }
            int newOverallProgress = totalProgressSum / totalLessons;
            enrollment.setProgress(newOverallProgress);
            enrollmentRepository.save(enrollment);
        }

        return ResponseEntity.ok(Map.of(
            "message", "Progress updated",
            "progressPercent", lp.getProgressPercent(),
            "completed", lp.isCompleted(),
            "courseProgress", enrollment.getProgress()
        ));
    }
}
