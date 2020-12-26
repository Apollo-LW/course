package com.apollo.course.service.impl;

import com.apollo.course.kafka.KafkaService;
import com.apollo.course.model.Course;
import com.apollo.course.model.ShareCourse;
import com.apollo.course.service.CourseService;
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
public class CourseServiceImpl implements CourseService {

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
    public Mono<Optional<Course>> getCourseById(String courseId) {
        return Mono.just(Optional.ofNullable(this.getCourseStateStore().get(courseId)));
    }

    @Override
    public Mono<Optional<Course>> saveCourse(Mono<Course> courseMono) {
        return this.kafkaService.sendCourseRecord(courseMono);
    }

    @Override
    public Mono<Boolean> updateCourse(Mono<Course> courseMono) {
        return courseMono.flatMap(course -> {
            Optional<Course> courseOptional = Optional.ofNullable(this.courseStateStore.get(course.getCourseId()));
            if (courseOptional.isEmpty()) return Mono.empty();
            Course updatedCourse = courseOptional.get();
            if (!updatedCourse.isActive()) return Mono.empty();
            updatedCourse.setCourseType(course.getCourseType());
            updatedCourse.setCourseName(course.getCourseName());
            updatedCourse.setCourseMembers(course.getCourseMembers());
            updatedCourse.setCourseOwners(course.getCourseOwners());
            updatedCourse.setCourseChapters(course.getCourseChapters());
            return this.kafkaService.sendCourseRecord(courseMono).map(Optional::isPresent);
        });
    }

    @Override
    public Mono<Boolean> shareCourse(Mono<ShareCourse> modifyCourseMono , boolean flag) {
        return modifyCourseMono.flatMap(shareCourse -> {
            Optional<Course> courseOptional = Optional.ofNullable(this.getCourseStateStore().get(shareCourse.getCourseId()));
            if (courseOptional.isEmpty()) return Mono.just(false);
            return Mono.just(courseOptional.get()).flatMap(updatedCourse -> {
                if (!updatedCourse.getCourseOwners().contains(shareCourse.getOwnerId())) return Mono.just(false);
                if (flag) updatedCourse.getCourseMembers().removeAll(shareCourse.getUserIds());
                else updatedCourse.getCourseMembers().addAll(shareCourse.getUserIds());
                return this.kafkaService.sendCourseRecord(Mono.just(updatedCourse)).map(Optional::isPresent);
            });
        });
    }

    @Override
    public Mono<Boolean> deleteCourse(Mono<ShareCourse> modifyCourseMono) {
        return modifyCourseMono.flatMap(shareCourse -> {
            Optional<Course> courseOptional = Optional.ofNullable(this.getCourseStateStore().get(shareCourse.getCourseId()));
            if (courseOptional.isEmpty()) return Mono.just(false);
            return Mono.just(courseOptional.get()).flatMap(course -> {
                if (!course.getCourseOwners().contains(shareCourse.getOwnerId())) return Mono.just(false);
                course.setActive(false);
                return this.kafkaService.sendCourseRecord(Mono.just(course)).map(Optional::isPresent);
            });
        });
    }

    @Override
    public Mono<Boolean> addMembers(Flux<String> membersIds , String courseId , String ownerId) {
        return membersIds.flatMap(memberId -> {
            Optional<Course> courseOptional = Optional.ofNullable(this.getCourseStateStore().get(courseId));
            if (courseOptional.isEmpty()) return Mono.just(false);
            Course course = courseOptional.get();
            if (!course.getCourseOwners().contains(ownerId)) return Mono.just(false);
            return this.kafkaService.sendCourseRecord(Mono.just(course.addMember(memberId))).map(Optional::isPresent);
        }).all(flag -> flag);
    }

    @Override
    public Mono<Boolean> addOwners(Flux<String> ownersIds , String courseId , String ownerId) {
        return ownersIds.flatMap(ownerIdToAdd -> {
            Optional<Course> courseOptional = Optional.ofNullable(this.getCourseStateStore().get(courseId));
            if (courseOptional.isEmpty()) return Mono.just(false);
            Course course = courseOptional.get();
            if (!course.getCourseOwners().contains(ownerId)) return Mono.just(false);
            return this.kafkaService.sendCourseRecord(Mono.just(course.addOwners(ownerId))).map(Optional::isPresent);
        }).all(flag -> flag);
    }
}
