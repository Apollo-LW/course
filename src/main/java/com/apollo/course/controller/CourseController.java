package com.apollo.course.controller;

import com.apollo.course.model.Share;
import com.apollo.course.service.CourseService;
import com.apollo.course.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.apollo.course.model.Course;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/course")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping("/{courseId}")
    public Mono<Course> getCourseById(@PathVariable("courseId") String courseId) {
        return this.courseService.getCourseById(courseId);
    }

    @GetMapping("/user/{userId}")
    public Flux<Course> getUserCoursesByUserId(@PathVariable("userId") String userId) {
        return this.userService.getUserCourses(userId);
    }

    @PostMapping("/")
    public Mono<Course> createCourse(@RequestBody Mono<Course> courseMono) {
        return this.courseService.saveCourse(courseMono);
    }

    @PutMapping("/")
    public Mono<Course> updateCourse(@RequestBody Mono<Course> courseMono) {
        return this.courseService.updateCourse(courseMono);
    }

    @PutMapping("/share/{flag}")
    public Mono<Boolean> shareCourse(@PathVariable("flag") boolean flag , @RequestBody Mono<Share> shareMono) {
        return this.courseService.shareCourse(shareMono , flag);
    }

}
