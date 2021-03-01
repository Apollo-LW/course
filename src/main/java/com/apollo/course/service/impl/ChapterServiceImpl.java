package com.apollo.course.service.impl;

import com.apollo.course.kafka.service.KafkaChapterService;
import com.apollo.course.model.*;
import com.apollo.course.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final KafkaChapterService kafkaChapterService;
    private final InteractiveQueryService interactiveQueryService;
    @Value("${chapter.kafka.store}")
    private String chapterTopicName;
    private ReadOnlyKeyValueStore<String, Chapter> chapterStateStore;

    private ReadOnlyKeyValueStore<String, Chapter> getChapterStateStore() {
        if (this.chapterStateStore == null)
            this.chapterStateStore = interactiveQueryService.getQueryableStore(this.chapterTopicName , QueryableStoreTypes.keyValueStore());
        return this.chapterStateStore;
    }

    private boolean isNotValid(final Optional<Chapter> chapterOptional , final String ownerId) {
        return chapterOptional.isEmpty() || chapterOptional.get().doesNotHaveOwner(ownerId);
    }

    @Override
    public Mono<Optional<Chapter>> saveChapter(final Mono<Chapter> chapterMono) {
        return this.kafkaChapterService.sendChapterRecord(chapterMono);
    }

    @Override
    public Mono<Optional<Chapter>> getChapterById(final String chapterId) {
        final Optional<Chapter> chapterOptional = Optional.ofNullable(this.getChapterStateStore().get(chapterId));
        if (chapterOptional.isEmpty()) return Mono.just(Optional.empty());
        return Mono.just(chapterOptional);
    }

    @Override
    public Mono<Boolean> updateChapter(final Mono<Chapter> chapterMono , final String ownerId) {
        return chapterMono.flatMap(chapter -> this.getChapterById(chapter.getChapterId()).flatMap(chapterOptional -> {
            if (this.isNotValid(chapterOptional , ownerId)) return Mono.just(false);
            final Chapter updatedChapter = chapterOptional.get();
            updatedChapter.setChapterName(chapter.getChapterName());
            updatedChapter.setChapterLecturesIds(chapter.getChapterLecturesIds());
            updatedChapter.setChapterResourcesIds(chapter.getChapterResourcesIds());
            return this.kafkaChapterService.sendChapterRecord(Mono.just(updatedChapter)).map(Optional::isPresent);
        }));
    }

    @Override
    public Mono<Boolean> deleteChapter(final String chapterId , final String ownerId) {
        return this.getChapterById(chapterId).flatMap(chapterOptional -> {
            if (this.isNotValid(chapterOptional , ownerId)) return Mono.just(false);
            Chapter chapter = chapterOptional.get();
            chapter.setActive(false);
            return this.kafkaChapterService.sendChapterRecord(Mono.just(chapter)).map(Optional::isPresent);
        });
    }

    @Override
    public Mono<Boolean> modifyLecture(Mono<ChapterLecture> chapterLectureMono) {
        return null;
    }

    @Override
    public Flux<Lecture> getChapterLectures(String chapterId) {
        return null;
    }

    @Override
    public Flux<Resource> getChapterResourcesByType(String chapterId , ResourceType resourceType) {
        return null;
    }
}
