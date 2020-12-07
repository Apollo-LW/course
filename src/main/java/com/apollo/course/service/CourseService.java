package com.apollo.course.service;

import com.apollo.course.model.Course;
import com.apollo.course.model.Share;
import reactor.core.publisher.Mono;

public interface CourseService {

    Mono<Course> getCourseById(String courseId);
    Mono<Course> saveCourse(Mono<Course> courseMono);
    Mono<Course> updateCourse(Mono<Course> courseMono);
    Mono<Boolean> shareCourse(Mono<Share> shareMono , boolean flag);
    Mono<Boolean> deleteCourse(String courseId);

}
