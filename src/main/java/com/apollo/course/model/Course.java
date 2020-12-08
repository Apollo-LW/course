package com.apollo.course.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Course {

    @Getter
    private String courseId;
    @Getter
    @Setter
    private String courseName, courseType;
    @Getter
    @Setter
    private Date courseDateOfCreation;
    @Getter
    @Setter
    private boolean isPublic, isActive;
    @Getter
    @Setter
    private HashSet<String> courseOwners, courseMembers;
    @Getter
    @Setter
    private HashSet<Lecture> courseLectures;

    public Course() {
        
    }

    public Course(List<String> ownerId) {
        this.isActive = true;
        this.courseOwners = new HashSet<>();
        courseOwners.addAll(ownerId);
        this.courseMembers = new HashSet<>();
        this.courseLectures = new HashSet<>();
        this.courseId = UUID.randomUUID().toString();
        this.courseDateOfCreation = Calendar.getInstance().getTime();
        this.courseName = this.courseId + '-' + this.courseDateOfCreation;
    }

    public void addMember(List<String> membersIds) {
        this.courseMembers.addAll(membersIds);
    }

    public void addOwners(List<String> ownersIds) {
        this.courseOwners.addAll(ownersIds);
    }

}
