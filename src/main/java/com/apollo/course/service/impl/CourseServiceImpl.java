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
    private ReadOnlyKeyValueStore<String, CourseEnrollment> courseEnrollmentRequestStateStore;

    private ReadOnlyKeyValueStore<String, Course> getCourseStateStore() {
        if (this.courseStateStore == null)
            this.courseStateStore = interactiveQueryService.getQueryableStore(this.courseStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.courseStateStore;
    }

    private ReadOnlyKeyValueStore<String, Chapter> getChapterStateStore() {
        if (this.chapterStateStore == null)
            this.chapterStateStore = interactiveQueryService.getQueryableStore(this.chapterStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.chapterStateStore;
    }

    private ReadOnlyKeyValueStore<String, CourseEnrollment> getCourseEnrollmentRequestStateStore() {
        if (this.courseEnrollmentRequestStateStore == null)
            this.courseEnrollmentRequestStateStore = interactiveQueryService.getQueryableStore(this.courseEnrollmentRequestStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.courseEnrollmentRequestStateStore;
    }

    private boolean isNotValid(final Optional<Course> optionalCourse , final String ownerId) {
        return optionalCourse.isEmpty() || !optionalCourse.get().isActive() || optionalCourse.get().doesNotHaveOwner(ownerId);
    }

    @Override
    public Mono<Optional<Course>> getCourseById(final String courseId) {
        return Mono.just(Optional.ofNullable(this.getCourseStateStore().get(courseId))).flatMap(courseOptional -> {
            if (courseOptional.isEmpty()) return Mono.just(Optional.empty());
            Course course = courseOptional.get();
            if (!course.isActive()) return Mono.just(Optional.empty());
            return Mono.just(Optional.of(course));
        });
    }

    @Override
    public Mono<Optional<Course>> saveCourse(final Mono<Course> courseMono) {
        return this.kafkaService.sendCourseRecord(courseMono);
    }

    @Override
    public Mono<Boolean> addChapter(final Mono<CourseChapter> courseChapterMono , final Boolean isAdd) {
        return courseChapterMono.flatMap(courseChapter ->
                this.getCourseById(courseChapter.getCourseId()).flatMap(courseOptional -> {
                    if (this.isNotValid(courseOptional , courseChapter.getOwnerId())) return Mono.just(false);
                    Course course = courseOptional.get();
                    if (isAdd) course.addChapter(courseChapter.getChapter());
                    else course.removeChapter(courseChapter.getChapter());
                    return this.kafkaService.sendCourseRecord(Mono.just(course)).map(Optional::isPresent);
                }));
    }

    @Override
    public Mono<Boolean> addLectureToChapter(final Mono<ChapterLecture> chapterLectureMono , final Boolean isAdd) {
        return chapterLectureMono.flatMap(chapterLecture ->
                this.getCourseById(chapterLecture.getCourseId()).flatMap(courseOptional -> {
                    if (this.isNotValid(courseOptional , chapterLecture.getOwnerId())) return Mono.just(false);
                    Course course = courseOptional.get();
                    Optional<Chapter> chapterOptional = Optional.ofNullable(this.getChapterStateStore().get(chapterLecture.getChapterId()));
                    if (chapterOptional.isEmpty()) return Mono.just(false);
                    Chapter chapter = chapterOptional.get();
                    course.removeChapter(chapter);
                    if (isAdd) chapter.addLecture(chapterLecture.getLecture());
                    else chapter.removeLecture(chapterLecture.getLecture());
                    course.addChapter(chapter);
                    return this.kafkaService.sendCourseRecord(Mono.just(course)).map(Optional::isPresent);
                }));
    }

    @Override
    public Mono<Boolean> createCourseEnrollmentRequest(final Mono<CourseEnrollmentRequest> enrollmentRequestMono) {
        return this.kafkaService.sendEnrollmentRequest(enrollmentRequestMono);
    }

    @Override
    public Flux<Chapter> getCourseChapters(final String courseId) {
        return this.getCourseById(courseId).flatMapMany(courseOptional -> {
            if (courseOptional.isEmpty()) return Flux.empty();
            return Flux.fromIterable(courseOptional.get().getCourseChapters());
        });
    }

    @Override
    public Flux<Lecture> getChapterLectures(final String chapterId) {
        Optional<Chapter> optionalChapter = Optional.ofNullable(this.getChapterStateStore().get(chapterId));
        if (optionalChapter.isEmpty()) return Flux.empty();
        return Flux.fromIterable(optionalChapter.get().getChapterLectures());
    }

    @Override
    public Flux<CourseEnrollmentRequest> getCourseEnrollmentRequests(final String courseId , final String ownerId) {
        Optional<CourseEnrollment> optionalCourseEnrollmentRequest = Optional.ofNullable(this.getCourseEnrollmentRequestStateStore().get(courseId));
        if (optionalCourseEnrollmentRequest.isEmpty()) return Flux.empty();
        CourseEnrollment courseEnrollment = optionalCourseEnrollmentRequest.get();
        return this.getCourseById(courseEnrollment.getCourseId())
                .flatMap(optionalCourse -> Mono.just(this.isNotValid(optionalCourse , ownerId)))
                .flatMapMany(isValid -> isValid ? Flux.fromIterable(courseEnrollment.getCourseEnrollmentRequests()) : Flux.empty());
    }

    @Override
    public Mono<Boolean> updateCourse(final Mono<Course> courseMono) {
        return courseMono.flatMap(course -> this.getCourseById(course.getCourseId()).flatMap(courseOptional -> {
            if (courseOptional.isEmpty()) return Mono.just(false);
            Course updatedCourse = courseOptional.get();
            updatedCourse.setCourseType(course.getCourseType());
            updatedCourse.setCourseName(course.getCourseName());
            updatedCourse.setCourseDescription(course.getCourseDescription());
            updatedCourse.setCourseCategory(course.getCourseCategory());
            updatedCourse.setCourseType(course.getCourseType());
            return this.kafkaService.sendCourseRecord(Mono.just(updatedCourse)).map(Optional::isPresent);
        }));
    }

    @Override
    public Mono<Boolean> deleteCourse(final Mono<ShareCourse> shareCourseMono) {
        return shareCourseMono.flatMap(shareCourse -> this.getCourseById(shareCourse.getCourseId()).flatMap(courseOptional -> {
            if (this.isNotValid(courseOptional , shareCourse.getOwnerId())) return Mono.just(false);
            Course course = courseOptional.get();
            course.setActive(false);
            return this.kafkaService.sendCourseRecord(Mono.just(course)).map(Optional::isPresent);
        }));
    }

    @Override
    public Mono<Boolean> addMembers(final Mono<ShareCourse> shareCourseMono , final Boolean isAdd) {
        return shareCourseMono.flatMap(shareCourse ->
                this.getCourseById(shareCourse.getCourseId()).flatMap(courseOptional -> {
                    if (this.isNotValid(courseOptional , shareCourse.getOwnerId())) return Mono.just(false);
                    Course course = courseOptional.get();
                    Boolean isAdded;
                    if (isAdd) isAdded = course.addMembers(shareCourse.getUserIds());
                    else isAdded = course.removeMembers(shareCourse.getUserIds());
                    return this.kafkaService.sendCourseRecord(Mono.just(course)).map(updatedCourseOptional -> updatedCourseOptional.isPresent() && isAdded);
                })
        );
    }

    @Override
    public Mono<Boolean> addOwners(final Mono<ShareCourse> shareCourseMono , final Boolean isAdd) {
        return shareCourseMono.flatMap(shareCourse ->
                this.getCourseById(shareCourse.getCourseId()).flatMap(courseOptional -> {
                    if (this.isNotValid(courseOptional , shareCourse.getOwnerId())) return Mono.just(false);
                    Course course = courseOptional.get();
                    Boolean isAdded;
                    if (isAdd) isAdded = course.addOwners(shareCourse.getUserIds());
                    else isAdded = course.removeOwners(shareCourse.getUserIds());
                    return this.kafkaService.sendCourseRecord(Mono.just(course)).map(updatedCourseOptional -> updatedCourseOptional.isPresent() && isAdded);
                }));
    }
}
