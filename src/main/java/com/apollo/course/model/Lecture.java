package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Data
public class Lecture {

    private String ownerId;
    private boolean isPublic, isActive = true;
    private String lectureId = UUID.randomUUID().toString();
    private HashSet<Resource> lectureResources = new HashSet<>();

    public Lecture addResource(Resource resource) {
        this.lectureResources.add(resource);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lecture lecture = (Lecture) o;
        return Objects.equals(lectureId , lecture.lectureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lectureId);
    }
}
