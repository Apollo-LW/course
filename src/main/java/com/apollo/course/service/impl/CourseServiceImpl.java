package com.apollo.course.service.impl;

import com.apollo.course.kafka.KafkaService;
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
    @Value("${chapter.kafka.store}")
    private String chapterStateStoreName;
    @Value("${course.kafka.enroll.store}")
    private String courseEnrollmentRequestStateStoreName;
    private final KafkaService kafkaService;
    private final InteractiveQueryService interactiveQueryService;
    private ReadOnlyKeyValueStore<String, Course> courseStateStore;
    private ReadOnlyKeyValueStore<String, Chapter> chapterStateStore;
    private ReadOnlyKeyValueStore<String, CourseEnrollmentRequest> courseEnrollmentRequestStateStore;

    private ReadOnlyKeyValueStore<String, Course> getCourseStateStore() {
        if (this.courseStateStore == null)
            this.courseStateStore = interactiveQueryService.getQueryableStore(this.courseStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.courseStateStore;
    }

    private ReadOnlyKeyValueStore<String, Chapter> getChapterStateStore() {
        if(this.chapterStateStore == null)
            this.chapterStateStore = interactiveQueryService.getQueryableStore(this.chapterStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.chapterStateStore;
    }

    private ReadOnlyKeyValueStore<String, CourseEnrollmentRequest> getCourseEnrollmentRequestStateStore() {
        if(this.courseEnrollmentRequestStateStore == null)
            this.courseEnrollmentRequestStateStore = interactiveQueryService.getQueryableStore(this.courseEnrollmentRequestStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.courseEnrollmentRequestStateStore;
    }

    private boolean isNotValid(Optional<Course> optionalCourse , String ownerId) {
        return optionalCourse.isEmpty() || optionalCourse.get().hasOwner(ownerId);
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
    public Mono<Boolean> addChapter(Mono<Chapter> chapterMono , String courseId , String ownerId) {
        Optional<Course> optionalCourse = Optional.ofNullable(this.getCourseStateStore().get(courseId));
        if(this.isNotValid(optionalCourse , ownerId)) return Mono.just(false);
        return chapterMono.flatMap(chapter -> this.kafkaService.sendCourseRecord(Mono.just(optionalCourse.get().addChapter(chapter))).map(Optional::isPresent));
    }

    @Override
    public Mono<Boolean> addLectureToChapter(Mono<Lecture> lectureMono , String courseId , String chapterId) {
        Optional<Chapter> optionalChapter = Optional.ofNullable(this.getChapterStateStore().get(chapterId));
        if (optionalChapter.isEmpty()) return Mono.just(false);
        Optional<Course> optionalCourse = Optional.ofNullable(this.getCourseStateStore().get(courseId));
        if(optionalCourse.isEmpty()) return Mono.just(false);
        Chapter chapter = optionalChapter.get();
        Course course = optionalCourse.get();
        Optional<Chapter> courseChapter = course.getChapter(chapter);
        if(courseChapter.isEmpty()) return Mono.just(false);
        course.removeChapter(chapter);
        return lectureMono.flatMap(lecture -> this.kafkaService.sendCourseRecord(Mono.just(course.addChapter(chapter.addLecture(lecture)))).map(Optional::isPresent));
    }

    @Override
    public Mono<Boolean> createCourseRequest(CourseEnrollment enrollment) {
        return this.kafkaService.sendEnrollmentRequest(Mono.just(enrollment));
    }

    @Override
    public Flux<Chapter> getCourseChapters(String courseId) {
        Optional<Course> optionalCourse = Optional.ofNullable(this.getCourseStateStore().get(courseId));
        if (optionalCourse.isEmpty()) return Flux.empty();
        return Flux.fromIterable(optionalCourse.get().getCourseChapters());
    }

    @Override
    public Flux<Lecture> getChapterLectures(String chapterId) {
        Optional<Chapter> optionalChapter = Optional.ofNullable(this.getChapterStateStore().get(chapterId));
        if(optionalChapter.isEmpty()) return Flux.empty();
        return Flux.fromIterable(optionalChapter.get().getChapterLectures());
    }

    @Override
    public Flux<CourseEnrollment> getCourseEnrollment(String courseId) {
        Optional<CourseEnrollmentRequest> optionalCourseEnrollmentRequest = Optional.ofNullable(this.getCourseEnrollmentRequestStateStore().get(courseId));
        if (optionalCourseEnrollmentRequest.isEmpty()) return Flux.empty();
        return Flux.fromIterable(optionalCourseEnrollmentRequest.get().getCourseEnrollments());
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
                if (!updatedCourse.hasOwner(shareCourse.getOwnerId())) return Mono.just(false);
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
                if (!course.hasOwner(shareCourse.getOwnerId())) return Mono.just(false);
                course.setActive(false);
                return this.kafkaService.sendCourseRecord(Mono.just(course)).map(Optional::isPresent);
            });
        });
    }

    @Override
    public Mono<Boolean> addMembers(Flux<String> membersIds , String courseId , String ownerId) {
        Optional<Course> optionalCourse = Optional.ofNullable(this.getCourseStateStore().get(courseId));
        if (this.isNotValid(optionalCourse , ownerId)) return Mono.just(false);
        Course course = optionalCourse.get();
        return membersIds
                .flatMap(memberId -> Mono.just(course.addMember(memberId) != null))
                .all(flag -> flag)
                .flatMap(result -> this.kafkaService.sendCourseRecord(Mono.just(course)).map(Optional::isPresent));
    }

    @Override
    public Mono<Boolean> addOwners(Flux<String> ownersIds , String courseId , String ownerId) {
        Optional<Course> optionalCourse = Optional.ofNullable(this.getCourseStateStore().get(courseId));
        if (this.isNotValid(optionalCourse , ownerId)) return Mono.just(false);
        Course course = optionalCourse.get();
        return ownersIds
                .flatMap(ownerIdToAdd -> Mono.just(course.addOwners(ownerIdToAdd) != null))
                .all(flag -> flag)
                .flatMap(result -> this.kafkaService.sendCourseRecord(Mono.just(course)).map(Optional::isPresent));
    }
}
