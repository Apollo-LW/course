package com.apollo.course.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChapterLecture {

    private final String courseId, ownerId, chapterId, lectureId;

}
