package com.apollo.course.service.user;

import com.apollo.course.model.Course;
import reactor.core.publisher.Flux;

public interface UserService {

    Flux<Course> getUserCourses(String userId);

}
