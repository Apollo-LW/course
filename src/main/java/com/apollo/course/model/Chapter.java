package com.apollo.course.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Chapter {

    private String chapterId = UUID.randomUUID().toString();
    private String chapterName;

}
