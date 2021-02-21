package com.apollo.course.kafka.service;

import com.apollo.course.model.CourseEnrollmentRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class KafkaEnrollmentService {

    private final KafkaSender<String, CourseEnrollmentRequest> courseEnrollmentRequestKafkaSender;
    @Value("${enroll.kafka.topic}")
    private String enrollmentTopicName;

    public Mono<Boolean> sendCourseEnrollmentRequestRecord(final Mono<CourseEnrollmentRequest> courseEnrollmentRequestMono) {
        return courseEnrollmentRequestMono.flatMap(courseEnrollmentRequest -> this.courseEnrollmentRequestKafkaSender
                .send(Mono.just(SenderRecord
                        .create(new ProducerRecord<String, CourseEnrollmentRequest>(this.enrollmentTopicName ,
                                        courseEnrollmentRequest.getRequestId() ,
                                        courseEnrollmentRequest) ,
                                courseEnrollmentRequest.getRequestId())))
                .next()
                .map(senderResult -> senderResult.exception() == null));
    }

}
