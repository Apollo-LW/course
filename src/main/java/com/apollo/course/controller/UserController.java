package com.apollo.course.controller;

import com.apollo.course.model.Course;
import com.apollo.course.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public Flux<Course> getUserCourse(@PathVariable("userId") String userId) {
        return this.userService.getUserCourses(userId);
    }

}
