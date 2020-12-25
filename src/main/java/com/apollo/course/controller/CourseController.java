package com.apollo.course.controller;

import com.apollo.course.model.Course;
import com.apollo.course.model.ModifyCourse;
import com.apollo.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/course")
public class CourseController {

    private final CourseService courseService;

    @GetMapping(value = "/{courseId}")
    public Mono<Course> getCourseById(@PathVariable("courseId") String courseId) {
        return this.courseService.getCourseById(courseId);
    }

    @PostMapping("/")
    public Mono<Course> createCourse(@RequestBody Course course) {
        return this.courseService.saveCourse(Mono.just(course));
    }

    @PutMapping(value = "/")
    public Mono<Boolean> updateCourse(@RequestBody Course course) {
        return this.courseService.updateCourse(Mono.just(course));
    }

    @PutMapping("/share/{flag}")
    public Mono<Boolean> shareCourse(@PathVariable("flag") Boolean flag , @RequestBody Mono<ModifyCourse> modifyCourseMono) {
        return this.courseService.shareCourse(modifyCourseMono , flag);
    }

    @DeleteMapping("")
    public Mono<Boolean> deleteCourse(@RequestBody Mono<ModifyCourse> modifyCourseMono) {
        return this.courseService.deleteCourse(modifyCourseMono);
    }
}
