package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CourseUser {

    private final Set<String> userCourses = new HashSet<>();
    private String userId;

    public CourseUser addCourse(final String courseId) {
        this.userCourses.add(courseId);
        return this;
    }
}
