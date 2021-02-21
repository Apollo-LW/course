package com.apollo.course.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
public class ChapterLecture {

    private final String ownerId, chapterId;
    private final Set<String> lectureIds;

}
