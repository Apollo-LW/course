package com.apollo.course.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;

@Data
public class CourseEnrollmentRequest {

    private String courseId, userId, ownerId;
    private Date requestDateOfCreation = Calendar.getInstance().getTime();
    private EnrollmentType enrollmentType;

}
