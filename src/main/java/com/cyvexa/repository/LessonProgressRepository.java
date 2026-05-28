package com.cyvexa.repository;

import com.cyvexa.model.LessonProgress;
import com.cyvexa.model.Enrollment;
import com.cyvexa.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findByEnrollment(Enrollment enrollment);
    Optional<LessonProgress> findByEnrollmentAndLesson(Enrollment enrollment, Lesson lesson);
    long countByEnrollmentAndCompletedTrue(Enrollment enrollment);
    long countByEnrollment(Enrollment enrollment);
    List<LessonProgress> findByLesson(com.cyvexa.model.Lesson lesson);
    void deleteByLesson(com.cyvexa.model.Lesson lesson);
    void deleteByEnrollment(com.cyvexa.model.Enrollment enrollment);
}
