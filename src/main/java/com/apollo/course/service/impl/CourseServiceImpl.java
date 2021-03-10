package com.apollo.course.service.impl;

import com.apollo.course.kafka.service.KafkaCourseService;
import com.apollo.course.model.*;
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
    private String courseStateStoreName;
    private final KafkaCourseService kafkaCourseService;
    private final InteractiveQueryService interactiveQueryService;
    private ReadOnlyKeyValueStore<String, Course> courseStateStore;

    private ReadOnlyKeyValueStore<String, Course> getCourseStateStore() {
        if (this.courseStateStore == null)
            this.courseStateStore = interactiveQueryService.getQueryableStore(this.courseStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.courseStateStore;
    }

    private boolean isNotValid(final Optional<Course> optionalCourse) {
        return optionalCourse.isEmpty() || !optionalCourse.get().isActive();
    }

    private boolean isNotValid(final Optional<Course> optionalCourse , final String ownerId) {
        return optionalCourse.isEmpty() || !optionalCourse.get().isActive() || optionalCourse.get().doesNotHaveOwner(ownerId);
    }

    @Override
    public Mono<Optional<Course>> getCourseById(final String courseId) {
        final Optional<Course> courseOptional = Optional.ofNullable(this.getCourseStateStore().get(courseId));
        if (this.isNotValid(courseOptional)) return Mono.just(Optional.empty());
        return Mono.just(courseOptional);
    }

    @Override
    public Mono<Optional<Course>> saveCourse(final Mono<Course> courseMono) {
        return this.kafkaCourseService.sendCourseRecord(courseMono);
    }

    @Override
    public Mono<Boolean> modifyChapters(final Mono<ModifyChapter> courseChapterMono , final boolean isAdd) {
        return courseChapterMono.flatMap(modifyChapter ->
                this.getCourseById(modifyChapter.getCourseId()).flatMap(courseOptional -> {
                    if (this.isNotValid(courseOptional , modifyChapter.getOwnerId())) return Mono.just(false);
                    Course course = courseOptional.get();
                    boolean isModified = course.modifyChapter(modifyChapter.getChapterId() , isAdd);
                    return this.kafkaCourseService.sendCourseRecord(Mono.just(course)).map(sendCourseOptional -> sendCourseOptional.isPresent() && isModified);
                }));
    }

    @Override
    public Flux<Chapter> getCourseChapters(final String courseId) {
        return null; //TODO
    }

    @Override
    public Flux<CourseEnrollmentRequest> getCourseEnrollmentRequests(final String courseId , final String ownerId) {
        return null; //TODO
    }

    @Override
    public Mono<Boolean> updateCourse(final Mono<Course> courseMono) {
        return courseMono.flatMap(course -> this.getCourseById(course.getCourseId()).flatMap(courseOptional -> {
            if (courseOptional.isEmpty()) return Mono.just(false);
            Course updatedCourse = courseOptional.get();
            if (course.getCourseType() != null) updatedCourse.setCourseType(course.getCourseType());
            if (course.getCourseName() != null) updatedCourse.setCourseName(course.getCourseName());
            if (course.getCourseDescription() != null)
                updatedCourse.setCourseDescription(course.getCourseDescription());
            if (course.getCourseCategory() != null) updatedCourse.setCourseCategory(course.getCourseCategory());
            if (course.getCourseType() != null) updatedCourse.setCourseType(course.getCourseType());
            return this.kafkaCourseService.sendCourseRecord(Mono.just(updatedCourse)).map(Optional::isPresent);
        }));
    }

    @Override
    public Mono<Boolean> deleteCourse(final Mono<ShareCourse> shareCourseMono) {
        return shareCourseMono.flatMap(shareCourse -> this.getCourseById(shareCourse.getCourseId()).flatMap(courseOptional -> {
            if (this.isNotValid(courseOptional , shareCourse.getOwnerId())) return Mono.just(false);
            Course course = courseOptional.get();
            course.setActive(false);
            return this.kafkaCourseService.sendCourseRecord(Mono.just(course)).map(Optional::isPresent);
        }));
    }

    @Override
    public Mono<Boolean> modifyMembers(final Mono<ShareCourse> shareCourseMono , final boolean isAdd) {
        return shareCourseMono.flatMap(shareCourse ->
                this.getCourseById(shareCourse.getCourseId()).flatMap(courseOptional -> {
                    if (this.isNotValid(courseOptional , shareCourse.getOwnerId())) return Mono.just(false);
                    Course course = courseOptional.get();
                    boolean isModified = course.modifyMembers(shareCourse.getUserIds() , isAdd);
                    return this.kafkaCourseService.sendCourseRecord(Mono.just(course)).map(updatedCourseOptional -> updatedCourseOptional.isPresent() && isModified);
                })
        );
    }

    @Override
    public Mono<Boolean> modifyOwners(final Mono<ShareCourse> shareCourseMono , final boolean isAdd) {
        return shareCourseMono.flatMap(shareCourse ->
                this.getCourseById(shareCourse.getCourseId()).flatMap(courseOptional -> {
                    if (this.isNotValid(courseOptional , shareCourse.getOwnerId())) return Mono.just(false);
                    Course course = courseOptional.get();
                    boolean isModified = course.modifyOwners(shareCourse.getUserIds() , isAdd);
                    return this.kafkaCourseService.sendCourseRecord(Mono.just(course)).map(updatedCourseOptional -> updatedCourseOptional.isPresent() && isModified);
                })
        );
    }
}
