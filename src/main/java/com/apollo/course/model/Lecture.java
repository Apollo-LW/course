package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.UUID;

@Data
public class Lecture {

    private String lectureId = UUID.randomUUID().toString();
    private boolean isPublic , isActive = true;
    private String ownerId;
    private HashSet<String> lectureVideos = new HashSet<>();
    private HashSet<String> lectureDocuments = new HashSet<>();

}
