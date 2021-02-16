package com.apollo.course.model;

import lombok.Data;

import java.util.*;

@Data
public class Course {

    private boolean isPublic, isActive = true;
    private EnrollmentType courseEnrollmentType = EnrollmentType.PUBLIC;
    private String courseId = UUID.randomUUID().toString();
    private HashSet<Chapter> courseChapters = new HashSet<>();
    private Date courseDateOfCreation = Calendar.getInstance().getTime();
    private HashSet<String> courseOwners = new HashSet<>(), courseMembers = new HashSet<>();
    private String courseName = this.courseId + '-' + this.courseDateOfCreation, courseType, courseDescription, courseCategory, courseRoomId;

    public Set<String> getAllCourseMembers() {
        HashSet<String> allCourseMembers = new HashSet<>(this.courseMembers);
        allCourseMembers.addAll(this.courseOwners);
        return allCourseMembers;
    }

    public void removeChapter(Chapter chapter) {
        this.courseChapters.remove(chapter);
    }

    public Boolean addMembers(Set<String> membersIds) {
        return this.courseMembers.addAll(membersIds);
    }

    public Boolean addOwners(Set<String> ownersIds) {
        return this.courseOwners.addAll(ownersIds);
    }

    public void addChapter(Chapter chapters) {
        this.courseChapters.add(chapters);
    }

    public Boolean removeMembers(HashSet<String> membersIds) {
        return this.courseMembers.removeAll(membersIds);
    }

    public Boolean removeOwners(Set<String> ownersIds) {
        return this.courseOwners.removeAll(ownersIds);
    }

    public Boolean doesNotHaveOwner(String ownerId) {
        return !this.courseOwners.contains(ownerId);
    }
}
