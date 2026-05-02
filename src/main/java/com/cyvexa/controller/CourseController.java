package com.cyvexa.controller;

import com.cyvexa.model.Course;
import com.cyvexa.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private com.cyvexa.repository.LessonRepository lessonRepository;

    @GetMapping
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        if (course.getLessons() != null) {
            for (com.cyvexa.model.Lesson lesson : course.getLessons()) {
                lesson.setCourse(course);
            }
        }
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return courseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/lessons")
    public List<com.cyvexa.model.Lesson> getCourseLessons(@PathVariable Long id) {
        return courseRepository.findById(id)
                .map(course -> lessonRepository.findByCourse(course))
                .orElse(List.of());
    }
}
