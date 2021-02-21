package com.apollo.course.model;

import lombok.Data;

@Data
public class ModifyChapter {

    private final String courseId, ownerId, chapterId;
}
