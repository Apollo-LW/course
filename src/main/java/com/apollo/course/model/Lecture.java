package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.UUID;

@Data
public class Lecture {

    private String ownerId;
    private boolean isPublic , isActive = true;
    private String lectureId = UUID.randomUUID().toString();
    private HashSet<Resource> lectureResources = new HashSet<>();

    public Lecture addResource(Resource resource) {
        this.lectureResources.add(resource);
        return this;
    }
}
