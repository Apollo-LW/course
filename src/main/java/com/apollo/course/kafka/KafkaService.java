package com.apollo.course.kafka;

import com.apollo.course.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@CommonsLog(topic = "Kafka Service")
public class KafkaService {

    @Value("${course.kafka.topic}")
    private String courseTopicName;
    private final KafkaSender<String, Course> courseKafkaSender;

    public Mono<Optional<Course>> sendCourseRecord(Mono<Course> courseMono) {
        return courseMono.flatMap(course -> this.courseKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<String, Course>(this.courseTopicName , course.getCourseId() , course) , 1)))
                .next()
                .doOnNext(log::info)
                .doOnError(log::error)
                .map(integerSenderResult -> integerSenderResult.exception() == null ? Optional.of(course) : Optional.empty()));
    }

}
