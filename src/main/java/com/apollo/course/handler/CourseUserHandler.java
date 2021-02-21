package com.apollo.course.handler;

import com.apollo.course.constant.RoutingConstant;
import com.apollo.course.model.Course;
import com.apollo.course.service.CourseUserService;
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
public class CourseUserHandler {

    private final CourseUserService courseUserService;

    public @NotNull Mono<ServerResponse> getUserCourses(final ServerRequest request) {
        final String userId = request.pathVariable(RoutingConstant.USER_ID);
        final Flux<Course> courseFlux = this.courseUserService.getUserCourses(userId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(courseFlux , Course.class);
    }

}
