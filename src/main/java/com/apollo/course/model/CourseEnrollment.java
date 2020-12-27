package com.apollo.course.model;

import lombok.Data;

@Data
public class CourseEnrollment {

    private String courseId , userId;
    private EnrollmentType enrollmentType;

}
