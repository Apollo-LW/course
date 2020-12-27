package com.apollo.course.controller;

import com.apollo.course.model.*;
import com.apollo.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/course")
public class CourseController {

    private final CourseService courseService;

    @GetMapping(value = "/{courseId}")
    public Mono<Course> getCourseById(@PathVariable("courseId") String courseId) {
        return this.courseService.getCourseById(courseId).map(Optional::get);
    }

    @GetMapping("/chapter/{courseId}")
    public Flux<Chapter> getCourseChapters(@PathVariable("courseId") String courseId) {
        return this.courseService.getCourseChapters(courseId);
    }

    @GetMapping("/chapter/lecture/{chapterId}")
    public Flux<Lecture> getChapterLectures(@PathVariable("chapterId") String chapterId) {
        return this.courseService.getChapterLectures(chapterId);
    }

    @GetMapping("/enroll/requests/{courseId}")
    public Flux<CourseEnrollment> getCourseEnrollment(@PathVariable("courseId") String courseId) {
        return this.courseService.getCourseEnrollment(courseId);
    }

    @PostMapping("/")
    public Mono<Course> createCourse(@RequestBody Course course) {
        return this.courseService.saveCourse(Mono.just(course)).map(Optional::get);
    }

    @PostMapping("/enroll")
    public Mono<Boolean> createEnrollment(@RequestBody CourseEnrollment courseEnrollment) {
        return this.courseService.createCourseRequest(courseEnrollment);
    }

    @PutMapping(value = "/")
    public Mono<Boolean> updateCourse(@RequestBody Course course) {
        return this.courseService.updateCourse(Mono.just(course));
    }

    @PutMapping("/share/{flag}")
    public Mono<Boolean> shareCourse(@PathVariable("flag") Boolean flag , @RequestBody Mono<ShareCourse> modifyCourseMono) {
        return this.courseService.shareCourse(modifyCourseMono , flag);
    }

    @PutMapping("/add/owner/{courseId}/{ownerId}")
    public Mono<Boolean> addOwnerToCourse(@PathVariable("courseId") String courseId , @PathVariable("ownerId") String ownerId , @RequestBody Flux<String> ownerIds) {
        return this.courseService.addOwners(ownerIds , courseId , ownerId);
    }

    @PutMapping("/add/member/{courseId}/{ownerId}")
    public Mono<Boolean> addMemberToCourse(@PathVariable("courseId") String courseId , @PathVariable("ownerId") String ownerId , @RequestBody Flux<String> membersIds) {
        return this.courseService.addMembers(membersIds , courseId , ownerId);
    }

    @PutMapping("/add/chapter/{courseId}/{ownerId}")
    public Mono<Boolean> addChapterToCourse(@PathVariable("courseId") String courseId , @PathVariable("ownerId") String ownerId , @RequestBody Mono<Chapter> chapterMono) {
        return this.courseService.addChapter(chapterMono , courseId , ownerId);
    }

    @PutMapping("/add/lecture/{courseId}/{chapterId}")
    public Mono<Boolean> addLectureToChapter(@PathVariable("chapterId") String chapterId , @PathVariable("courseId") String courseId , @RequestBody Mono<Lecture> lectureMono) {
        return this.courseService.addLectureToChapter(lectureMono , courseId , chapterId);
    }

    @DeleteMapping("/")
    public Mono<Boolean> deleteCourse(@RequestBody Mono<ShareCourse> modifyCourseMono) {
        return this.courseService.deleteCourse(modifyCourseMono);
    }
}
