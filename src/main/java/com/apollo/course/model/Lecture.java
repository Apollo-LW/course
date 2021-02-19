package com.apollo.course.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
public class Lecture {

    private final @NotNull @NotEmpty String ownerId;
    private final String lectureId = UUID.randomUUID().toString();
    private Set<String> lectureResourcesIds = new HashSet<>();
    private boolean isPublic, isActive = true;

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
