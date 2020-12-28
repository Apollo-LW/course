package com.apollo.course.service;

import com.apollo.course.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CourseService {

    Flux<Chapter> getCourseChapters(String courseId);

    Flux<Lecture> getChapterLectures(String chapterId);

    Flux<CourseEnrollmentRequest> getCourseEnrollment(String courseId);

    Mono<Boolean> updateCourse(Mono<Course> courseMono);

    Mono<Optional<Course>> getCourseById(String courseId);

    Mono<Optional<Course>> saveCourse(Mono<Course> courseMono);

    Mono<Boolean> deleteCourse(Mono<ShareCourse> modifyCourseMono);

    Mono<Boolean> shareCourse(Mono<ShareCourse> modifyCourseMono , boolean flag);

    Mono<Boolean> addOwners(Flux<String> ownersIds , String courseId , String ownerId);

    Mono<Boolean> addMembers(Flux<String> membersIds , String courseId , String ownerId);

    Mono<Boolean> addChapter(Mono<Chapter> chapterMono , String courseId , String ownerId);

    Mono<Boolean> addLectureToChapter(Mono<Lecture> lectureMono , String courseId , String chapterId);

    Mono<Boolean> createCourseRequest(CourseEnrollmentRequest enrollment);
}
