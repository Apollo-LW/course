package com.apollo.course.config;

import com.apollo.course.constant.RoutingConstant;
import com.apollo.course.handler.CourseUserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CourseUserRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routeCourseUser(CourseUserHandler courseUserHandler) {
        return RouterFunctions
                .route()
                .path(RoutingConstant.COURSE_USER_PATH , routeFunctionBuilder ->
                        routeFunctionBuilder.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON) ,
                                builder -> builder.GET(RoutingConstant.USER_ID_PATH , courseUserHandler::getUserCourses)))
                .build();
    }

}
