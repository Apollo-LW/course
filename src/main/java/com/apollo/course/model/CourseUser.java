package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class CourseUser {

    private String userId;
    private HashSet<Course> userCourses = new HashSet<>();

    public CourseUser addCourse(Course course) {
        this.userCourses.add(course);
        return this;
    }

}
