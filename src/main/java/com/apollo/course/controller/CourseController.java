package com.apollo.course.controller;

import com.apollo.course.model.Share;
import com.apollo.course.service.CourseService;
import com.apollo.course.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.apollo.course.model.Course;

import javax.validation.constraints.NotNull;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/course")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final static Random random = new Random();

    private String getRandomEle(@NotNull List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    @GetMapping
    public Mono<Course> createUser() {
        final List<String> names = Arrays.asList("Idea101" , "Arabic99" , "English101" , "CS11435" , "CS50");
        final List<String> ids = new ArrayList<>();
        for(int i = 0 ; i < random.nextInt(100) ; ++i) ids.add(UUID.randomUUID().toString());
        Course course = new Course();
        course.setCourseName(getRandomEle(names));
        course.setCourseType("Educational");
        HashSet<String> owners = new HashSet<>();
        HashSet<String> members = new HashSet<>();
        for(int i = 0 ; i < random.nextInt(10) ; ++i) owners.add(getRandomEle(ids));
        for(int i = 0 ; i < random.nextInt(10) ; ++i) members.add(getRandomEle(ids));
        course.setCourseOwners(owners);
        course.setCourseMembers(members);
        return this.courseService.saveCourse(Mono.just(course));
    }

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

    @PutMapping(value = "/" , produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Course> updateCourse(@RequestBody Mono<Course> courseMono) {
        return this.courseService.updateCourse(courseMono);
    }

    @PutMapping("/share/{flag}")
    public Mono<Boolean> shareCourse(@PathVariable("flag") boolean flag , @RequestBody Mono<Share> shareMono) {
        return this.courseService.shareCourse(shareMono , flag);
    }

}
