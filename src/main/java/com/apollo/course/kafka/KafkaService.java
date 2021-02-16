package com.apollo.course.kafka;

import com.apollo.course.model.Course;
import com.apollo.course.model.CourseEnrollmentRequest;
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
public class KafkaService {

    @Value("${course.kafka.topic}")
    private String courseTopicName;
    @Value("${course.kafka.enroll.topic}")
    private String courseEnrollmentTopicName;
    private final KafkaSender<String, Course> courseKafkaSender;
    private final KafkaSender<String, CourseEnrollmentRequest> courseEnrollmentKafkaSender;

    public Mono<Optional<Course>> sendCourseRecord(final Mono<Course> courseMono) {
        return courseMono.flatMap(course -> this.courseKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<String, Course>(this.courseTopicName , course.getCourseId() , course) , course.getCourseId())))
                .next()
                .map(senderResult -> senderResult.exception() == null ? Optional.of(course) : Optional.empty()));
    }

    public Mono<Boolean> sendEnrollmentRequest(final Mono<CourseEnrollmentRequest> courseEnrollmentMono) {
        return courseEnrollmentMono.flatMap(courseEnrollment -> this.courseEnrollmentKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<String, CourseEnrollmentRequest>(this.courseEnrollmentTopicName , courseEnrollment.getCourseId() , courseEnrollment) , courseEnrollment.getCourseId())))
                .next()
                .map(senderResult -> senderResult.exception() == null)
        );
    }

}
