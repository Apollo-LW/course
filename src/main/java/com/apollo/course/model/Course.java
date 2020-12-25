package com.apollo.course.model;

import lombok.Data;

import java.util.*;

@Data
public class Course {

    private boolean isPublic, isActive = true;
    private String courseId =  UUID.randomUUID().toString();
    private HashSet<Lecture> courseLectures = new HashSet<>();
    private Date courseDateOfCreation = Calendar.getInstance().getTime();
    private HashSet<String> courseOwners = new HashSet<>() , courseMembers = new HashSet<>();
    private String courseName = this.courseId + '-' + this.courseDateOfCreation , courseType;

    public void addMember(List<String> membersIds) {
        this.courseMembers.addAll(membersIds);
    }

    public void addOwners(List<String> ownersIds) {
        this.courseOwners.addAll(ownersIds);
    }

}
