package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class CourseEnrollment {

    private String courseId;
    private HashSet<CourseEnrollmentRequest> courseEnrollmentRequests = new HashSet<>();

    public CourseEnrollment addCourseEnrollment(CourseEnrollmentRequest courseEnrollmentRequest) {
        this.courseEnrollmentRequests.add(courseEnrollmentRequest);
        return this;
    }

}
