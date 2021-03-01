package com.apollo.course.service;

import reactor.core.publisher.Flux;

public interface CourseUserService {

    Flux<String> getUserCourses(final String userId);

}
