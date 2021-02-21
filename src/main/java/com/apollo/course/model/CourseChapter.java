package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CourseChapter {

    private final Set<Chapter> courseChapters = new HashSet<>();
    private String courseId, courseName, chapterId, chapterName;

    public void addChapter(Chapter chapter) {
        this.courseChapters.add(chapter);
    }

    public void removeChapter(Chapter chapter) {
        this.courseChapters.remove(chapter);
    }

}
