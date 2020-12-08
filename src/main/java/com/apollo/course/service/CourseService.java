package com.apollo.course.service;

import com.apollo.course.model.Course;
import com.apollo.course.model.ModifyCourse;
import reactor.core.publisher.Mono;

public interface CourseService {

    Mono<Course> getCourseById(String courseId);
    Mono<Course> saveCourse(Mono<Course> courseMono);
    Mono<Boolean> updateCourse(Mono<Course> courseMono);
    Mono<Boolean> shareCourse(Mono<ModifyCourse> modifyCourseMono , boolean flag);
    Mono<Boolean> deleteCourse(Mono<ModifyCourse> modifyCourseMono);

}
