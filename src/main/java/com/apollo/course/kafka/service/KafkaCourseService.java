package com.apollo.course.kafka.service;

import com.apollo.course.model.Course;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KafkaCourseService {

    private final KafkaSender<String, Course> courseKafkaSender;
    @Value("${course.kafka.topic}")
    private String courseTopicName;

    public Mono<Optional<Course>> sendCourseRecord(final Mono<Course> courseMono) {
        return courseMono.flatMap(course -> this.courseKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<String, Course>(this.courseTopicName , course.getCourseId() , course) , course.getCourseId())))
                .next()
                .map(senderResult -> senderResult.exception() == null ? Optional.of(course) : Optional.empty()));
    }
}
