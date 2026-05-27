package com.cyvexa.repository;

import com.cyvexa.model.Enrollment;
import com.cyvexa.model.User;
import com.cyvexa.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUser(User user);
    Optional<Enrollment> findByUserAndCourse(User user, Course course);
    boolean existsByUserAndCourse(User user, Course course);
    long countByCourse(Course course);
    long countByCourseAndProgress(Course course, int progress);
}
