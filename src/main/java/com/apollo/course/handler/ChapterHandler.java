package com.apollo.course.handler;

import com.apollo.course.constant.RoutingConstant;
import com.apollo.course.model.Chapter;
import com.apollo.course.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ChapterHandler {

    private final ChapterService chapterService;

    public @NotNull Mono<ServerResponse> getChapterById(final ServerRequest request) {
        final String chapterId = request.pathVariable(RoutingConstant.CHAPTER_ID);
        final Mono<Chapter> chapterMono = this.chapterService.getChapterById(chapterId).flatMap(Mono::justOrEmpty);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(chapterMono , Chapter.class);
    }

    public @NotNull Mono<ServerResponse> createChapter(final ServerRequest request) {
        final Mono<Chapter> chapterMono = request.bodyToMono(Chapter.class);
        final Mono<Chapter> createdChapterMono = this.chapterService.saveChapter(chapterMono).flatMap(Mono::justOrEmpty);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(createdChapterMono , Chapter.class);
    }

    public @NotNull Mono<ServerResponse> updateChapter(final ServerRequest request) {
        final String ownerId = request.pathVariable(RoutingConstant.OWNER_ID);
        final Mono<Chapter> chapterMono = request.bodyToMono(Chapter.class);
        final Mono<Boolean> isUpdated = this.chapterService.updateChapter(chapterMono , ownerId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isUpdated , Boolean.class);
    }

    public @NotNull Mono<ServerResponse> deleteChapter(final ServerRequest request) {
        final String chapterId = request.pathVariable(RoutingConstant.CHAPTER_ID);
        final String ownerId = request.pathVariable(RoutingConstant.OWNER_ID);
        final Mono<Boolean> isDeleted = this.chapterService.deleteChapter(chapterId , ownerId);
        return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(isDeleted , Boolean.class);
    }

}
