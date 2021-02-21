package com.apollo.course.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
public class Chapter {

    private final @NotNull @NotEmpty String chapterOwnerId;
    private final String chapterId = UUID.randomUUID().toString();
    private @NotNull @NotEmpty String chapterName;
    private Set<String> chapterLecturesIds = new HashSet<>();
    private Set<String> chapterResourcesIds = new HashSet<>();
    private boolean isActive = true;

    public void addResource(final String resourceId) {
        this.chapterResourcesIds.add(resourceId);
    }

    public void removeResource(final String resourceId) {
        this.chapterResourcesIds.add(resourceId);
    }

    public void addLecture(final String lectureId) {
        this.chapterLecturesIds.add(lectureId);
    }

    public void removeLecture(final String lectureId) {
        this.chapterLecturesIds.remove(lectureId);
    }

    public boolean doesNotHaveOwner(final String ownerId) {
        return !this.chapterOwnerId.equals(ownerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chapter chapter = (Chapter) o;
        return Objects.equals(chapterId , chapter.chapterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chapterId);
    }
}
