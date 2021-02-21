package com.apollo.course.service;

import com.apollo.course.model.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CourseService {

    Mono<Optional<Course>> saveCourse(final Mono<Course> courseMono);

    Mono<Optional<Course>> getCourseById(final String courseId);

    Mono<Boolean> updateCourse(final Mono<Course> courseMono);

    Mono<Boolean> deleteCourse(final Mono<ShareCourse> modifyCourseMono);

    Mono<Boolean> modifyOwners(final Mono<ShareCourse> shareCourseMono , final boolean isAdd);

    Mono<Boolean> modifyMembers(final Mono<ShareCourse> shareCourseMono , final boolean isAdd);

    Mono<Boolean> modifyChapters(final Mono<ModifyChapter> courseChapterMono , final boolean isAdd);

    Flux<Chapter> getCourseChapters(final String courseId);

    Flux<CourseEnrollmentRequest> getCourseEnrollmentRequests(final String courseId , final String ownerId);
}
