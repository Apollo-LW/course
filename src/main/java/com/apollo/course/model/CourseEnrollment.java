package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CourseEnrollment {

    private final String courseId;
    private final Set<CourseEnrollmentRequest> courseEnrollmentRequests = new HashSet<>();

}
