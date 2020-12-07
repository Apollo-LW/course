package com.apollo.course.model;

import lombok.Data;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

@Data
public class Course {

    private String courseId , courseName , courseType;
    private Date courseDateOfCreation;
    private boolean isPublic , isActive;
    private HashSet<String> courseOwners, courseMembers;
    private HashSet<Lecture> courseLectures;

    public Course() {
        this.isActive = true;
        this.courseOwners = new HashSet<>();
        this.courseMembers = new HashSet<>();
        this.courseLectures = new HashSet<>();
        this.courseId = UUID.randomUUID().toString();
        this.courseDateOfCreation = Calendar.getInstance().getTime();
        this.courseName = this.courseId + '-' + this.courseDateOfCreation;
    }

}
