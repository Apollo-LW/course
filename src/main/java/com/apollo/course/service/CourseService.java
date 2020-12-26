package com.apollo.course.service;

import com.apollo.course.model.Course;
import com.apollo.course.model.ShareCourse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface CourseService {

    Mono<Optional<Course>> getCourseById(String courseId);
    Mono<Optional<Course>> saveCourse(Mono<Course> courseMono);
    Mono<Boolean> updateCourse(Mono<Course> courseMono);
    Mono<Boolean> shareCourse(Mono<ShareCourse> modifyCourseMono , boolean flag);
    Mono<Boolean> deleteCourse(Mono<ShareCourse> modifyCourseMono);
    Mono<Boolean> addMembers(Flux<String> membersIds , String courseId , String ownerId);
    Mono<Boolean> addOwners(Flux<String> ownersIds , String courseId , String ownerId);

}
