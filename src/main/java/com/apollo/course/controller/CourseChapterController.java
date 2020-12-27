package com.apollo.course.controller;

import com.apollo.course.model.Chapter;
import com.apollo.course.service.CourseChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/course/chapter")
@RequiredArgsConstructor
public class CourseChapterController {

    private final CourseChapterService courseChapterService;

    @GetMapping("/{courseId}")
    public Flux<Chapter> getCourseChapters(@PathVariable("courseId") String courseId) {
        return this.courseChapterService.getCourseChapters(courseId);
    }

    @PutMapping("/{courseId}/{ownerId}")
    public Mono<Boolean> addChapterCourse(@PathVariable("courseId") String courseId , @PathVariable("ownerId") String ownerId , @RequestBody Mono<Chapter> chapterMono) {
        return this.courseChapterService.addChapter(chapterMono , courseId , ownerId);
    }

    //TODO: Update Chapter, Delete Chapter, Add Lecture

}
