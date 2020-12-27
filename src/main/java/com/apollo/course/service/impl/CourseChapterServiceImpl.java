package com.apollo.course.service.impl;

import com.apollo.course.kafka.KafkaService;
import com.apollo.course.model.Chapter;
import com.apollo.course.model.Course;
import com.apollo.course.service.CourseChapterService;
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
public class CourseChapterServiceImpl implements CourseChapterService {

    @Value("${course.kafka.store}")
    String courseStateStoreName;
    private final KafkaService kafkaService;
    private final InteractiveQueryService interactiveQueryService;
    private ReadOnlyKeyValueStore<String, Course> courseStateStore;

    private ReadOnlyKeyValueStore<String, Course> getCourseStateStore() {
        if (this.courseStateStore == null)
            this.courseStateStore = interactiveQueryService.getQueryableStore(this.courseStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.courseStateStore;
    }

    @Override
    public Flux<Chapter> getCourseChapters(String courseId) {
        return Flux.fromIterable(this.getCourseStateStore().get(courseId).getCourseChapters());
    }

    @Override
    public Mono<Boolean> addChapter(Mono<Chapter> chapterMono , String courseId , String ownerId) {
        return chapterMono.flatMap(chapter -> {
            Optional<Course> courseOptional = Optional.ofNullable(this.getCourseStateStore().get(courseId));
            if (courseOptional.isEmpty()) return Mono.empty();
            return Mono.just(courseOptional.get()).flatMap(course -> {
                if (!course.getCourseOwners().contains(ownerId)) return Mono.just(false);
                return this.kafkaService.sendCourseRecord(Mono.just(course.addChapter(chapter))).map(Optional::isPresent);
            });
        });
    }



}
