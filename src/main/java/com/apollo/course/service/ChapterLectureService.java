package com.apollo.course.service;

import com.apollo.course.model.Lecture;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChapterLectureService {

    Flux<Lecture> getChapterLectures(String chapterId);
    Mono<Boolean> addLecture(Mono<Lecture> lectureMono);

}
