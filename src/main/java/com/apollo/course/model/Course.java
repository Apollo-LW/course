package com.apollo.course.model;

import lombok.Data;

import java.util.*;

@Data
public class Course {

    private boolean isPublic, isActive = true;
    private String courseId =  UUID.randomUUID().toString();
    private HashSet<Chapter> courseChapters = new HashSet<>();
    private Date courseDateOfCreation = Calendar.getInstance().getTime();
    private HashSet<String> courseOwners = new HashSet<>() , courseMembers = new HashSet<>();
    private String courseName = this.courseId + '-' + this.courseDateOfCreation , courseType;

    public Course addMember(String membersId) {
        this.courseMembers.add(membersId);
        return this;
    }

    public Course addOwners(String ownersId) {
        this.courseOwners.add(ownersId);
        return this;
    }

    public Course addChapter(Chapter chapters) {
        this.courseChapters.add(chapters);
        return this;
    }

}
