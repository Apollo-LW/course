package com.apollo.course.service;

import com.apollo.course.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CourseService {

    Flux<Chapter> getCourseChapters(final String courseId);

    Flux<Lecture> getChapterLectures(final String chapterId);

    Flux<CourseEnrollmentRequest> getCourseEnrollmentRequests(final String courseId , final String ownerId);

    Mono<Boolean> updateCourse(final Mono<Course> courseMono);

    Mono<Optional<Course>> getCourseById(final String courseId);

    Mono<Optional<Course>> saveCourse(final Mono<Course> courseMono);

    Mono<Boolean> deleteCourse(final Mono<ShareCourse> modifyCourseMono);

    Mono<Boolean> addOwners(final Mono<ShareCourse> shareCourseMono , final Boolean isAdd);

    Mono<Boolean> addMembers(final Mono<ShareCourse> shareCourseMono , final Boolean isAdd);

    Mono<Boolean> addChapter(final Mono<CourseChapter> courseChapterMono , final Boolean isAdd);

    Mono<Boolean> addLectureToChapter(final Mono<ChapterLecture> chapterLectureMono , final Boolean isAdd);

    Mono<Boolean> createCourseEnrollmentRequest(final Mono<CourseEnrollmentRequest> enrollmentRequestMono);
}
