package com.apollo.course.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;

@Data
public class CourseEnrollmentRequest {

    private final String courseId, userId, ownerId;
    private final Date requestDateOfCreation = Calendar.getInstance().getTime();
    private final EnrollmentType enrollmentType;

}
