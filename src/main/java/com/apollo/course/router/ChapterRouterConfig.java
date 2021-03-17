package com.apollo.course.router;

import com.apollo.course.constant.RoutingConstant;
import com.apollo.course.handler.ChapterHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ChapterRouterConfig {

    @Bean
    public RouterFunction<ServerResponse> routeChapter(final ChapterHandler chapterHandler) {
        return RouterFunctions
                .route()
                .path(RoutingConstant.CHAPTER , routeFunctionBuilder ->
                        routeFunctionBuilder.nest(RequestPredicates.accept(MediaType.APPLICATION_JSON) , builder -> builder
                                .GET(RoutingConstant.CHAPTER_ID_PATH , chapterHandler::getChapterById)
                                .POST(chapterHandler::createChapter)
                                .PUT(RoutingConstant.OWNER_ID_PATH , chapterHandler::updateChapter)
                                .DELETE(RoutingConstant.CHAPTER_OWNER_ID_PATH , chapterHandler::deleteChapter)))
                .build();
    }

}
