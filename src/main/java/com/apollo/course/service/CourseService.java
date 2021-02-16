package com.apollo.course.service;

import com.apollo.course.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CourseService {

    Flux<Chapter> getCourseChapters(String courseId);

    Flux<Lecture> getChapterLectures(String chapterId);

    Flux<CourseEnrollmentRequest> getCourseEnrollmentRequests(String courseId , String ownerId);

    Mono<Boolean> updateCourse(Mono<Course> courseMono);

    Mono<Optional<Course>> getCourseById(String courseId);

    Mono<Optional<Course>> saveCourse(Mono<Course> courseMono);

    Mono<Boolean> deleteCourse(Mono<ShareCourse> modifyCourseMono);

    Mono<Boolean> addOwners(Mono<ShareCourse> shareCourseMono , Boolean isAdd);

    Mono<Boolean> addMembers(Mono<ShareCourse> shareCourseMono , Boolean isAdd);

    Mono<Boolean> addChapter(Mono<CourseChapter> courseChapterMono , Boolean isAdd);

    Mono<Boolean> addLectureToChapter(Mono<ChapterLecture> chapterLectureMono , Boolean isAdd);

    Mono<Boolean> createCourseEnrollmentRequest(Mono<CourseEnrollmentRequest> enrollmentRequestMono);
}
