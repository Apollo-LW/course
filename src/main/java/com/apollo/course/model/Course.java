package com.apollo.course.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

@Data
public class Course {

    private String courseId = UUID.randomUUID().toString() , courseName , courseType;
    private Date courseDateOfCreation = Calendar.getInstance().getTime();
    private boolean isPublic , isActive = true;
    private HashSet<String> courseOwners = new HashSet<>() , courseMembers = new HashSet<>();
    private HashSet<Lecture> courseLectures = new HashSet<>();

    public Course() {
        this.courseName = this.courseId + '-' + this.courseDateOfCreation;
    }

}
