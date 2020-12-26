package com.apollo.course.service;

import com.apollo.course.model.Chapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CourseChapterService {

    Flux<Chapter> getCourseChapters(String courseId);
    Mono<Boolean> addChapter(Mono<Chapter> chapterMono , String courseId , String ownerId);
}
