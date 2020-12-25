package com.apollo.course.service;

import com.apollo.course.model.Course;
import reactor.core.publisher.Flux;

public interface UserService {

    Flux<Course> getUserCourses(String userId);

}
