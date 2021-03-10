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

    public @NotNull Mono<ServerResponse> getCourseById(final ServerRequest request) {
        final String courseId = request.pathVariable(RoutingConstant.COURSE_ID);
        final Mono<Course> courseMono = this.courseService.getCourseById(courseId).flatMap(Mono::justOrEmpty);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(courseMono , Course.class);
    }

    public @NotNull Mono<ServerResponse> getCourseChapters(final ServerRequest request) {
        final String courseId = request.pathVariable(RoutingConstant.COURSE_ID);
        final Flux<Chapter> chapterFlux = this.courseService.getCourseChapters(courseId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(chapterFlux , Chapter.class);
    }

    public @NotNull Mono<ServerResponse> getCourseEnrollment(final ServerRequest request) {
        final String courseId = request.pathVariable(RoutingConstant.COURSE_ID);
        final String ownerId = request.pathVariable(RoutingConstant.OWNER_ID);
        final Flux<CourseEnrollmentRequest> courseEnrollmentRequestFlux = this.courseService.getCourseEnrollmentRequests(courseId , ownerId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(courseEnrollmentRequestFlux , CourseEnrollmentRequest.class);
    }

    public @NotNull Mono<ServerResponse> createCourse(final ServerRequest request) {
        final Mono<Course> courseMono = request.bodyToMono(Course.class);
        Mono<Course> createdCourseMono = this.courseService.saveCourse(courseMono).flatMap(Mono::justOrEmpty);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdCourseMono , Course.class);
    }

    public @NotNull Mono<ServerResponse> updateCourse(final ServerRequest request) {
        final Mono<Course> courseMono = request.bodyToMono(Course.class);
        final Mono<Boolean> updateCourseStatus = this.courseService.updateCourse(courseMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(updateCourseStatus , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> modifyCourseOwners(final ServerRequest request) {
        final Mono<ShareCourse> shareCourseMono = request.bodyToMono(ShareCourse.class);
        final boolean isAdd = Boolean.parseBoolean(request.pathVariable(RoutingConstant.FLAG));
        final Mono<Boolean> isOwnersAdded = this.courseService.modifyOwners(shareCourseMono , isAdd);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isOwnersAdded , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> modifyCourseMembers(final ServerRequest request) {
        final Mono<ShareCourse> shareCourseMono = request.bodyToMono(ShareCourse.class);
        final boolean isAdd = Boolean.parseBoolean(request.pathVariable(RoutingConstant.FLAG));
        final Mono<Boolean> isMembersAdded = this.courseService.modifyMembers(shareCourseMono , isAdd);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isMembersAdded , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> modifyCourseChapter(final ServerRequest request) {
        final Mono<ModifyChapter> courseChapterMono = request.bodyToMono(ModifyChapter.class);
        final boolean isAdd = Boolean.parseBoolean(request.pathVariable(RoutingConstant.FLAG));
        final Mono<Boolean> isChapterAdded = this.courseService.modifyChapters(courseChapterMono , isAdd);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isChapterAdded , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> deleteCourse(final ServerRequest request) {
        final Mono<ShareCourse> shareCourseMono = request.bodyToMono(ShareCourse.class);
        final Mono<Boolean> isDeleted = this.courseService.deleteCourse(shareCourseMono);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isDeleted , Boolean.class);
    }
}
