package com.cyvexa.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column
    private String authorName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String whatYouWillLearn;

    @Column(columnDefinition = "TEXT")
    private String outcomes;

    @Column(columnDefinition = "TEXT")
    private String modules;

    private String thumbnailUri;
    private String type = "Free";

    @JsonManagedReference
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Lesson> lessons;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Enrollment> enrollments;

    @com.fasterxml.jackson.annotation.JsonProperty("enrollmentCount")
    public int getEnrollmentCount() {
        return enrollments != null ? enrollments.size() : 0;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("trendingScore")
    public int getTrendingScore() {
        int enrollCount = enrollments != null ? enrollments.size() : 0;
        int lessonCount = lessons != null ? lessons.size() : 0;
        return enrollCount * 3 + lessonCount;
    }
}
