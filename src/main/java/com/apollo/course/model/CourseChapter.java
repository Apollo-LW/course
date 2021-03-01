package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CourseChapter {

    private final Set<String> courseChapters = new HashSet<>();
    private String courseId, courseName, chapterId, chapterName;

    public void addChapter(final String chapterId) {
        this.courseChapters.add(chapterId);
    }

    public void removeChapter(final String chapterId) {
        this.courseChapters.remove(chapterId);
    }

}
