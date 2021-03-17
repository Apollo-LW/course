package com.apollo.course.router;

import com.apollo.course.constant.RoutingConstant;
import com.apollo.course.handler.CourseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CourseRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routeCourse(final CourseHandler courseHandler) {
        return RouterFunctions
                .route()
                .path(RoutingConstant.COURSE_PATH , routeFunctionBuilder ->
                        routeFunctionBuilder.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON) ,
                                builder -> builder
                                        .GET(RoutingConstant.COURSE_ID_PATH , courseHandler::getCourseById)
                                        .GET(RoutingConstant.COURSE_CHAPTERS , courseHandler::getCourseChapters)
/*                                        .GET(RoutingConstant.CHAPTER_LECTURES , courseHandler::getChapterLectures)*/
                                        .GET(RoutingConstant.COURSE_ENROLLMENT , courseHandler::getCourseEnrollment)
                                        .POST(courseHandler::createCourse)
/*                                        .POST(RoutingConstant.COURSE_ENROLL_REQUEST , courseHandler::createCourseEnrollment)*/
                                        .PUT(courseHandler::updateCourse)
                                        .PATCH(RoutingConstant.ADD_OWNER , courseHandler::modifyCourseOwners)
                                        .PATCH(RoutingConstant.ADD_MEMBER , courseHandler::modifyCourseMembers)
/*                                        .PATCH(RoutingConstant.ADD_LECTURE , courseHandler::modifyChapterLectures)*/
                                        .PATCH(RoutingConstant.ADD_CHAPTER , courseHandler::modifyCourseChapter)
                                        .DELETE(courseHandler::deleteCourse)))
                .build();
    }
}
