package com.apollo.course.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Data
public class CourseEnrollmentRequest {

    private final String requestId = UUID.randomUUID().toString();
    private final String courseId, userId, ownerId;
    private final Date requestDateOfCreation = Calendar.getInstance().getTime();
    private final EnrollmentType enrollmentType;

}
