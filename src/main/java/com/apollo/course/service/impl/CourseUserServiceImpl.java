package com.apollo.course.service.impl;

import com.apollo.course.model.CourseUser;
import com.apollo.course.service.CourseUserService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseUserServiceImpl implements CourseUserService {

    @Value("${user.kafka.store}")
    private String courseUserStateStoreName;
    private ReadOnlyKeyValueStore<String, CourseUser> courseUserStateStore;
    private final InteractiveQueryService interactiveQueryService;

    private ReadOnlyKeyValueStore<String, CourseUser> getCourseUserStateStore() {
        if (this.courseUserStateStore == null)
            this.courseUserStateStore = this.interactiveQueryService.getQueryableStore(this.courseUserStateStoreName , QueryableStoreTypes.keyValueStore());
        return this.courseUserStateStore;
    }

    @Override
    public Flux<String> getUserCourses(final String userId) {
        Optional<CourseUser> optionalCourseUser = Optional.ofNullable(this.getCourseUserStateStore().get(userId));
        if (optionalCourseUser.isEmpty()) return Flux.empty();
        return Flux.fromIterable(optionalCourseUser.get().getUserCourses());
    }
}
