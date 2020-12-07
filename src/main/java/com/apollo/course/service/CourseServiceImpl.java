package com.apollo.course.service;

import com.apollo.course.kafka.KafkaService;
import com.apollo.course.model.Course;
import com.apollo.course.model.Share;
import com.apollo.course.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
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
    private final InteractiveQueryService interactiveQueryService;
    private final UserService userService;
    @Value("${course.kafka.store}")
    String courseStateStoreName;
    private ReadOnlyKeyValueStore<String , Course> courseStateStore;

    private ReadOnlyKeyValueStore<String , Course> getCourseStateStore() {
        if(this.courseStateStore == null) this.courseStateStore = interactiveQueryService.getQueryableStore(this.courseStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.courseStateStore;
    }

    @Override
    public Mono<Course> getCourseById(String courseId) {
        Optional<Course> course = Optional.ofNullable(this.getCourseStateStore().get(courseId));
        if(course.isEmpty()) return Mono.empty();
        Course idCourse = course.get();
        return idCourse.isActive() ? Mono.just(idCourse) : Mono.empty();
    }

    @Override
    public Mono<Course> saveCourse(Mono<Course> courseMono) {
        return courseMono.flatMap(course -> this.kafkaService.sendCourseRecord(courseMono).map(Optional::get));
    }

    @Override
    public Mono<Course> updateCourse(Mono<Course> courseMono) {
        return courseMono.flatMap(course -> {
            Optional<Course> courseOptional = Optional.ofNullable(this.courseStateStore.get(course.getCourseId()));
            if (courseOptional.isEmpty()) return Mono.empty();
            Course updatedCourse = courseOptional.get();
            if (!updatedCourse.isActive()) return Mono.empty();
            updatedCourse.setCourseType(course.getCourseType());
            updatedCourse.setCourseName(course.getCourseName());
            updatedCourse.setCourseMembers(course.getCourseMembers());
            updatedCourse.setCourseOwners(course.getCourseOwners());
            updatedCourse.setCourseLectures(course.getCourseLectures());
            return this.kafkaService.sendCourseRecord(courseMono).map(Optional::get);
        });
    }

    @Override
    public Mono<Boolean> shareCourse(Mono<Share> shareMono , boolean flag) {
        return shareMono.flatMap(share -> {
            Optional<Course> course = Optional.ofNullable(this.getCourseStateStore().get(share.getCourseId()));
            if(course.isEmpty()) return Mono.just(false);
            return Mono.just(course.get()).flatMap(updatedCourse -> {
                if(!updatedCourse.getCourseOwners().contains(share.getOwnerId())) return Mono.just(false);
                updatedCourse.getCourseMembers().addAll(share.getUserIds());
                return this.kafkaService.sendCourseRecord(Mono.just(updatedCourse)).map(Optional::isPresent);
            });
        });
    }


    @Override
    public Mono<Boolean> deleteCourse(String courseId) {
        Optional<Course> course = Optional.ofNullable(this.getCourseStateStore().get(courseId));
        if(course.isEmpty()) return Mono.just(false);
        Course deletedCourse = course.get();
        deletedCourse.setActive(false);
        return this.kafkaService.sendCourseRecord(Mono.just(deletedCourse)).map(Optional::isPresent);
    }
}
