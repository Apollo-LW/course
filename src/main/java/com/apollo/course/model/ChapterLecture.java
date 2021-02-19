package com.apollo.course.model;

import lombok.Data;

@Data
public class ChapterLecture {

    private String courseId, ownerId, chapterId;
    private Lecture lecture;


}
