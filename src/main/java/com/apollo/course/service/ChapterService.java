package com.apollo.course.service;

import com.apollo.course.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface ChapterService {

    Mono<Optional<Chapter>> saveChapter(final Mono<Chapter> chapterMono);

    Mono<Optional<Chapter>> getChapterById(final String chapterId);

    Mono<Boolean> updateChapter(final Mono<Chapter> chapterMono , final String ownerId);

    Mono<Boolean> deleteChapter(final String chapterId , final String ownerId);

    Mono<Boolean> modifyLecture(final Mono<ChapterLecture> chapterLectureMono);

    Flux<Lecture> getChapterLectures(final String chapterId);

    Flux<Resource> getChapterResourcesByType(final String chapterId , final ResourceType resourceType);

}
