package com.apollo.course.service.user;

import com.apollo.course.model.Course;
import com.apollo.course.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final InteractiveQueryService userInteractiveQueryService;
    @Value("${user.kafka.store}")
    private String userStateStoreName;
    private ReadOnlyKeyValueStore<String , User> userStateStore;

    @Override
    public Flux<Course> getUserCourses(String userId) {
        return Flux.fromIterable(this.userStateStore.get(userId).getUserCourses());
    }
}
