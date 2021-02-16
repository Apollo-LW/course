package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.UUID;

@Data
public class Chapter {

    private String chapterName;
    private String chapterId = UUID.randomUUID().toString();
    private HashSet<Lecture> chapterLectures = new HashSet<>();
    private HashSet<Resource> chapterResources = new HashSet<>();

    public Chapter addResource(Resource resource) {
        this.chapterResources.add(resource);
        return this;
    }

    public void addLecture(Lecture lecture) {
        this.chapterLectures.add(lecture);
    }

    public void removeLecture(Lecture lecture) {
        this.chapterLectures.remove(lecture);
    }

}
