package com.apollo.course.controller;

import com.apollo.course.model.Course;
import com.apollo.course.model.ModifyCourse;
import com.apollo.course.service.CourseService;
import com.apollo.course.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/course")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @GetMapping(value = "/{courseId}" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Course> getCourseById(@PathVariable("courseId") String courseId) {
        return this.courseService.getCourseById(courseId);
    }

    @GetMapping(value = "/user/{userId}" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Course> getUserCoursesByUserId(@PathVariable("userId") String userId) {
        return this.userService.getUserCourses(userId);
    }

    @PostMapping(value = "/" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Course> createCourse(@RequestBody Mono<Course> courseMono) {
        return this.courseService.saveCourse(courseMono);
    }

    @PutMapping(value = "/")
    public Mono<Boolean> updateCourse(@RequestBody Mono<Course> courseMono) {
        return this.courseService.updateCourse(courseMono);
    }

    @PutMapping("/share/{flag}")
    public Mono<Boolean> shareCourse(@PathVariable("flag") boolean flag , @RequestBody Mono<ModifyCourse> shareMono) {
        return this.courseService.shareCourse(shareMono , flag);
    }

    @DeleteMapping("")
    public Mono<Boolean> deleteCourse(@RequestBody Mono<ModifyCourse> modifyCourseMono) {
        return this.courseService.deleteCourse(modifyCourseMono);
    }
}
