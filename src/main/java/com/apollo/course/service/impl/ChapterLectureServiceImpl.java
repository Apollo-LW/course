package com.apollo.course.service.impl;

import com.apollo.course.kafka.KafkaService;
import com.apollo.course.model.Chapter;
import com.apollo.course.model.Lecture;
import com.apollo.course.service.ChapterLectureService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChapterLectureServiceImpl implements ChapterLectureService {

    @Value("${chapter.kafka.store}")
    private String chapterStateStoreName;
    private final KafkaService kafkaService;
    private final InteractiveQueryService interactiveQueryService;
    private ReadOnlyKeyValueStore<String, Chapter> chapterStateStore;

    private ReadOnlyKeyValueStore<String, Chapter> getChapterStateStore() {
        if (this.chapterStateStore == null)
            this.chapterStateStore = interactiveQueryService.getQueryableStore(this.chapterStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.chapterStateStore;
    }


    @Override
    public Flux<Lecture> getChapterLectures(String chapterId) {
        return Flux.fromIterable(this.getChapterStateStore().get(chapterId).getChapterLectures());
    }

    @Override
    public Mono<Boolean> addLecture(Mono<Lecture> lectureMono) {
        //TODO
        return null;
    }
}
