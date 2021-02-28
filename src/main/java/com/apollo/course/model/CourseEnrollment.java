package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CourseEnrollment {

    private final Set<String> courseEnrollmentRequests = new HashSet<>();
    private String courseId;

    public CourseEnrollment addCourseEnrollmentRequest(final String courseEnrollmentRequestId) {
        this.courseEnrollmentRequests.add(courseEnrollmentRequestId);
        return this;
    }
}
