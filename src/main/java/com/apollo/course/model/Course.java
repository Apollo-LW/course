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

    public Course addMember(List<String> membersIds) {
        this.courseMembers.addAll(membersIds);
        return this;
    }

    public Course addOwners(List<String> ownersIds) {
        this.courseOwners.addAll(ownersIds);
        return this;
    }

    public Course addChapter(List<Chapter> chapters) {
        this.courseChapters.addAll(chapters);
        return this;
    }

}
