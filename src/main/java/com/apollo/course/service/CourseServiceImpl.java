package com.apollo.course.service;

import com.apollo.course.kafka.KafkaService;
import com.apollo.course.model.Course;
import com.apollo.course.model.ModifyCourse;
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

    @Value("${course.kafka.store}")
    String courseStateStoreName;
    private final KafkaService kafkaService;
    private final InteractiveQueryService interactiveQueryService;
    private final UserService userService;
    private ReadOnlyKeyValueStore<String , Course> courseStateStore;

    private ReadOnlyKeyValueStore<String , Course> getCourseStateStore() {
        if(this.courseStateStore == null)
            this.courseStateStore = interactiveQueryService.getQueryableStore(this.courseStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.courseStateStore;
    }

    @Override
    public Mono<Course> getCourseById(String courseId) {
        Optional<Course> courseOptional = Optional.ofNullable(this.getCourseStateStore().get(courseId));
        if(courseOptional.isEmpty()) return Mono.empty();
        Course course = courseOptional.get();
        return course.isActive() ? Mono.just(course) : Mono.empty();
    }

    @Override
    public Mono<Course> saveCourse(Mono<Course> courseMono) {
        return this.kafkaService.sendCourseRecord(courseMono).map(Optional::get);
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
            updatedCourse.setCourseLectures(course.getCourseLectures());
            return this.kafkaService.sendCourseRecord(courseMono).map(Optional::isPresent);
        });
    }

    @Override
    public Mono<Boolean> shareCourse(Mono<ModifyCourse> modifyCourseMono , boolean flag) {
        return modifyCourseMono.flatMap(modifyCourse -> {
            Optional<Course> courseOptional = Optional.ofNullable(this.getCourseStateStore().get(modifyCourse.getCourseId()));
            if(courseOptional.isEmpty()) return Mono.just(false);
            return Mono.just(courseOptional.get()).flatMap(updatedCourse -> {
                if(!updatedCourse.getCourseOwners().contains(modifyCourse.getOwnerId())) return Mono.just(false);
                if(flag) updatedCourse.getCourseMembers().removeAll(modifyCourse.getUserIds());
                else updatedCourse.getCourseMembers().addAll(modifyCourse.getUserIds());
                return this.kafkaService.sendCourseRecord(Mono.just(updatedCourse)).map(Optional::isPresent);
            });
        });
    }

    @Override
    public Mono<Boolean> deleteCourse(Mono<ModifyCourse> modifyCourseMono) {
        return modifyCourseMono.flatMap(modifyCourse -> {
            Optional<Course> courseOptional = Optional.ofNullable(this.getCourseStateStore().get(modifyCourse.getCourseId()));
            if(courseOptional.isEmpty()) return Mono.just(false);
            return Mono.just(courseOptional.get()).flatMap(course -> {
                if (!course.getCourseOwners().contains(modifyCourse.getOwnerId())) return Mono.just(false);
                course.setActive(false);
                return this.kafkaService.sendCourseRecord(Mono.just(course)).map(Optional::isPresent);
            });
        });
    }
}
