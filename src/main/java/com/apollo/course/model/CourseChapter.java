package com.apollo.course.model;

import lombok.Data;

@Data
public class CourseChapter {

    private String courseId, ownerId;
    private Chapter chapter;
}
