package com.apollo.course.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Data
public class Chapter {

    private final String chapterId = UUID.randomUUID().toString();
    private @NotNull @NotEmpty String chapterName;
    private Set<String> chapterLecturesIds = new HashSet<>();
    private Set<String> chapterResourcesIds = new HashSet<>();

    public void addResource(final String resourceId) {
        this.chapterLecturesIds.add(resourceId);
    }

    public void addLecture(final String lectureId) {
        this.chapterLecturesIds.add(lectureId);
    }

    public void removeLecture(final String lectureId) {
        this.chapterLecturesIds.remove(lectureId);
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
