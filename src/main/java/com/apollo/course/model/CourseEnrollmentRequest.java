package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class CourseEnrollmentRequest {

    private String courseId;
    private HashSet<CourseEnrollment> courseEnrollments = new HashSet<>();

    public CourseEnrollmentRequest addCourseEnrollment(CourseEnrollment courseEnrollment) {
        this.courseEnrollments.add(courseEnrollment);
        return this;
    }

}
