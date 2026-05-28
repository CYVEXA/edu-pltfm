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

    @Autowired
    private com.cyvexa.repository.LessonProgressRepository lessonProgressRepository;

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
    @org.springframework.transaction.annotation.Transactional
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

            // In-place merging of lessons to preserve existing IDs and avoid foreign key constraint violations
            java.util.List<com.cyvexa.model.Lesson> updatedLessons = updatedCourse.getLessons();
            java.util.List<com.cyvexa.model.Lesson> existingLessons = existingCourse.getLessons();

            java.util.Set<Long> retainedLessonIds = new java.util.HashSet<>();
            java.util.List<com.cyvexa.model.Lesson> lessonsToKeep = new java.util.ArrayList<>();

            if (updatedLessons != null) {
                for (com.cyvexa.model.Lesson updatedL : updatedLessons) {
                    if (updatedL.getId() != null) {
                        // Lesson already exists: update properties in-place
                        for (com.cyvexa.model.Lesson existingL : existingLessons) {
                            if (existingL.getId().equals(updatedL.getId())) {
                                existingL.setTitle(updatedL.getTitle());
                                if (updatedL.getVideoPath() != null && !updatedL.getVideoPath().isEmpty()) {
                                    existingL.setVideoPath(updatedL.getVideoPath());
                                }
                                existingL.setDuration(updatedL.getDuration());
                                lessonsToKeep.add(existingL);
                                retainedLessonIds.add(existingL.getId());
                                break;
                            }
                        }
                    } else {
                        // New lesson: link to course
                        updatedL.setCourse(existingCourse);
                        lessonsToKeep.add(updatedL);
                    }
                }
            }

            // Remove/delete orphaned lessons and their associated progress records
            for (com.cyvexa.model.Lesson existingL : new java.util.ArrayList<>(existingLessons)) {
                if (!retainedLessonIds.contains(existingL.getId())) {
                    lessonProgressRepository.deleteByLesson(existingL);
                    lessonRepository.delete(existingL);
                }
            }

            existingLessons.clear();
            existingLessons.addAll(lessonsToKeep);

            return ResponseEntity.ok(courseRepository.save(existingCourse));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return courseRepository.findById(id).map(course -> {
            for (com.cyvexa.model.Lesson lesson : course.getLessons()) {
                lessonProgressRepository.deleteByLesson(lesson);
            }
            courseRepository.delete(course);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
