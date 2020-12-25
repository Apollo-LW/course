package com.apollo.course.service.impl;

import com.apollo.course.model.Course;
import com.apollo.course.model.CourseUser;
import com.apollo.course.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

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
    public Flux<Course> getUserCourses(String userId) {
        return Flux.fromIterable(this.getCourseUserStateStore().get(userId).getUserCourses());
    }
}
