package com.apollo.course.service;

import com.apollo.course.kafka.KafkaService;
import com.apollo.course.model.Course;
import com.apollo.course.model.Share;
import com.apollo.course.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final KafkaService kafkaService;
    private final InteractiveQueryService courseInteractiveQueryService;
    private final UserService userService;
    @Value("${course.kafka.store}")
    private String courseStateStoreName;
    private ReadOnlyKeyValueStore<String , Course> courseStateStore;

    @Override
    public Mono<Course> getCourseById(String courseId) {
        return Mono.just(this.courseStateStore.get(courseId));
    }

    @Override
    public Mono<Course> saveCourse(Mono<Course> courseMono) {
        return courseMono.flatMap(course -> this.kafkaService.sendCourseRecord(courseMono).map(Optional::get));
    }

    @Override
    public Mono<Course> updateCourse(Mono<Course> courseMono) {
        return courseMono.flatMap(course -> this.kafkaService.sendCourseRecord(courseMono).map(Optional::get));
    }

    @Override
    public Mono<Boolean> shareCourse(Mono<Share> shareMono , boolean flag) {
        return shareMono.flatMap(share -> {
            Optional<Course> course = Optional.ofNullable(this.courseStateStore.get(share.getCourseId()));
            if(course.isEmpty()) return Mono.just(false);
            return Mono.just(course.get()).flatMap(updatedCourse -> {
                if(!updatedCourse.getCourseMembers().contains(share.getOwnerId())) return Mono.empty();
                updatedCourse.getCourseMembers().addAll(share.getUserIds());
                return this.kafkaService.sendCourseRecord(Mono.just(updatedCourse)).map(Optional::isPresent);
            });
        });
    }


    @Override
    public Mono<Boolean> deleteCourse(String courseId) {
        return this.kafkaService.sendCourseRecord(Mono.just(this.courseStateStore.get(courseId))).map(Optional::isPresent);
    }
}
