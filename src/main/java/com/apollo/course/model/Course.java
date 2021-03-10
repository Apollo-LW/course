package com.apollo.course.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

@Data
public class Course {

    private final String courseId = UUID.randomUUID().toString();
    private EnrollmentType courseEnrollmentType = EnrollmentType.PUBLIC;
    private Date courseDateOfCreation = Calendar.getInstance().getTime();
    private @NotNull @NotEmpty String courseName = this.courseId + '-' + this.courseDateOfCreation.toString().replaceAll(" " , "");
    private @NotNull @NotEmpty String courseType;
    private @NotNull @NotEmpty String courseDescription;
    private @NotNull @NotEmpty String courseCategory;
    private @NotNull @NotEmpty String courseRoomId;
    private Set<String> courseChaptersIds = new HashSet<>();
    private Set<String> courseOwners = new HashSet<>(), courseMembers = new HashSet<>();
    private boolean isPublic, isActive = true;

    public boolean modifyChapter(String chapterId , boolean isAdd) {
        return isAdd ? this.courseChaptersIds.add(chapterId) : this.courseChaptersIds.remove(chapterId);
    }

    public boolean modifyMembers(Set<String> membersIds , boolean isAdd) {
        return isAdd ? this.courseMembers.addAll(membersIds) : this.courseMembers.removeAll(membersIds);
    }

    public boolean modifyOwners(Set<String> ownerIds , boolean isAdd) {
        return isAdd ? this.courseOwners.addAll(ownerIds) : this.courseOwners.removeAll(ownerIds);
    }

    public boolean doesNotHaveOwner(String ownerId) {
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
