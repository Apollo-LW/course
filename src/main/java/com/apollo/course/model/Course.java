package com.apollo.course.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotEmpty;
import java.util.*;

@Data
public class Course {

    private final String courseId = UUID.randomUUID().toString();
    private EnrollmentType courseEnrollmentType = EnrollmentType.PUBLIC;
    private @NotNull @NotEmpty String courseName = this.courseId + '-' + this.courseDateOfCreation;
    private @NotNull @NotEmpty String courseType;
    private @NotNull @NotEmpty String courseDescription;
    private @NotNull @NotEmpty String courseCategory;
    private @NotNull @NotEmpty String courseRoomId;
    private Set<String> courseChaptersIds = new HashSet<>();
    private Date courseDateOfCreation = Calendar.getInstance().getTime();
    private Set<String> courseOwners = new HashSet<>(), courseMembers = new HashSet<>();
    private boolean isPublic, isActive = true;

    public void removeChapter(String chapterId) {
        this.courseChaptersIds.remove(chapterId);
    }

    public Boolean addMembers(Set<String> membersIds) {
        return this.courseMembers.addAll(membersIds);
    }

    public Boolean addOwners(Set<String> ownersIds) {
        return this.courseOwners.addAll(ownersIds);
    }

    public void addChapter(String chapterId) {
        this.courseChaptersIds.add(chapterId);
    }

    public Boolean removeMembers(Set<String> membersIds) {
        return this.courseMembers.removeAll(membersIds);
    }

    public Boolean removeOwners(Set<String> ownersIds) {
        return this.courseOwners.removeAll(ownersIds);
    }

    public Boolean doesNotHaveOwner(String ownerId) {
        return !this.courseOwners.contains(ownerId);
    }

    public Set<String> getAllCourseMembers() {
        HashSet<String> allCourseMembers = new HashSet<>(this.courseMembers);
        allCourseMembers.addAll(this.courseOwners);
        return allCourseMembers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(courseId , course.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
