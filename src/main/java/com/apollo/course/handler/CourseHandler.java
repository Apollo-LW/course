package com.apollo.course.handler;

import com.apollo.course.constant.RoutingConstant;
import com.apollo.course.model.*;
import com.apollo.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CourseHandler {

    private final CourseService courseService;

    public @NotNull Mono<ServerResponse> getCourseById(ServerRequest request) {
        final String courseId = request.pathVariable(RoutingConstant.COURSE_ID);
        final Mono<Course> courseMono = this.courseService.getCourseById(courseId).flatMap(Mono::justOrEmpty);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(courseMono , Course.class);
    }

    public @NotNull Mono<ServerResponse> getCourseChapters(ServerRequest request) {
        final String courseId = request.pathVariable(RoutingConstant.COURSE_ID);
        final Flux<Chapter> chapterFlux = this.courseService.getCourseChapters(courseId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(chapterFlux , Chapter.class);
    }

    public @NotNull Mono<ServerResponse> getChapterLectures(ServerRequest request) {
        final String chapterId = request.pathVariable(RoutingConstant.CHAPTER_ID);
        final Flux<Lecture> lectureFlux = this.courseService.getChapterLectures(chapterId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(lectureFlux , Lecture.class);
    }

    public @NotNull Mono<ServerResponse> getCourseEnrollment(ServerRequest request) {
        final String courseId = request.pathVariable(RoutingConstant.COURSE_ID);
        final String ownerId = request.pathVariable(RoutingConstant.OWNER_ID);
        final Flux<CourseEnrollmentRequest> courseEnrollmentRequestFlux = this.courseService.getCourseEnrollmentRequests(courseId , ownerId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(courseEnrollmentRequestFlux , CourseEnrollmentRequest.class);
    }

    public @NotNull Mono<ServerResponse> createCourse(ServerRequest request) {
        Mono<Course> courseMono = request.bodyToMono(Course.class);
        courseMono = this.courseService.saveCourse(courseMono).flatMap(Mono::justOrEmpty);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(courseMono , Course.class);
    }

    public @NotNull Mono<ServerResponse> createCourseEnrollment(ServerRequest request) {
        final Mono<CourseEnrollmentRequest> courseEnrollmentRequestMono = request.bodyToMono(CourseEnrollmentRequest.class);
        final Mono<Boolean> courseEnrollmentRequestStatus = this.courseService.createCourseEnrollmentRequest(courseEnrollmentRequestMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(courseEnrollmentRequestStatus , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> updateCourse(ServerRequest request) {
        final Mono<Course> courseMono = request.bodyToMono(Course.class);
        final Mono<Boolean> updateCourseStatus = this.courseService.updateCourse(courseMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateCourseStatus , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> addOwnerToCourse(ServerRequest request) {
        final Mono<ShareCourse> shareCourseMono = request.bodyToMono(ShareCourse.class);
        final Mono<Boolean> isOwnersAdded = this.courseService.addOwners(shareCourseMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isOwnersAdded , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> addMembersToCourse(ServerRequest request) {
        final Mono<ShareCourse> shareCourseMono = request.bodyToMono(ShareCourse.class);
        final Mono<Boolean> isMembersAdded = this.courseService.addMembers(shareCourseMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isMembersAdded , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> addChapterToCourse(ServerRequest request) {
        final Mono<CourseChapter> courseChapterMono = request.bodyToMono(CourseChapter.class);
        final Mono<Boolean> isChapterAdded = this.courseService.addChapter(courseChapterMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isChapterAdded , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> addLectureToChapter(ServerRequest request) {
        final Mono<ChapterLecture> chapterLectureMono = request.bodyToMono(ChapterLecture.class);
        Mono<Boolean> isLectureAdded = this.courseService.addLectureToChapter(chapterLectureMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isLectureAdded , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> deleteCourse(ServerRequest request) {
        final Mono<ShareCourse> shareCourseMono = request.bodyToMono(ShareCourse.class);
        Mono<Boolean> isDeleted = this.courseService.deleteCourse(shareCourseMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isDeleted , Boolean.class);
    }
}
