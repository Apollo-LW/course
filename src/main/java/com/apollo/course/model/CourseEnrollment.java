package com.apollo.course.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;

@Data
public class CourseEnrollment {

    private String courseId , userId;
    private Date requestDateOfCreation = Calendar.getInstance().getTime();
    private EnrollmentType enrollmentType;

}
