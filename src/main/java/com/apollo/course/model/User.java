package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class User {

    private String userId;
    private HashSet<Course> userCourses = new HashSet<>();

}
