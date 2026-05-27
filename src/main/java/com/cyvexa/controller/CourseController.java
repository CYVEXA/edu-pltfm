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

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        return courseRepository.findById(id).map(existingCourse -> {
            existingCourse.setTitle(updatedCourse.getTitle());
            existingCourse.setAuthorName(updatedCourse.getAuthorName());
            existingCourse.setDescription(updatedCourse.getDescription());
            existingCourse.setWhatYouWillLearn(updatedCourse.getWhatYouWillLearn());
            existingCourse.setOutcomes(updatedCourse.getOutcomes());
            if (updatedCourse.getThumbnailUri() != null) {
                existingCourse.setThumbnailUri(updatedCourse.getThumbnailUri());
            }
            
            existingCourse.getLessons().clear();
            if (updatedCourse.getLessons() != null) {
                for (com.cyvexa.model.Lesson lesson : updatedCourse.getLessons()) {
                    lesson.setCourse(existingCourse);
                    existingCourse.getLessons().add(lesson);
                }
            }
            return ResponseEntity.ok(courseRepository.save(existingCourse));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return courseRepository.findById(id).map(course -> {
            courseRepository.delete(course);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
