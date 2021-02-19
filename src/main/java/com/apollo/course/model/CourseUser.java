package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CourseUser {

    private final String userId;
    private final Set<Course> userCourses = new HashSet<>();

}
