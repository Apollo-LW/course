package com.apollo.course.kafka;

import com.apollo.course.model.Course;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CommonsLog(topic = "Kafka Service")
public class KafkaService {

    private final KafkaReceiver<String , Course> courseKafkaReceiver;
    private final KafkaSender<String , Course> courseKafkaSender;
    @Value("${course.kafka.topic}")
    private String topicName;
    @Getter
    private ConnectableFlux<ServerSentEvent<Course>> courseEventPublisher;

    @PostConstruct
    public void init() {
        this.courseEventPublisher = courseKafkaReceiver
                .receive()
                .map(courseRecord -> ServerSentEvent.builder(courseRecord.value()).build())
                .publish();
        this.courseEventPublisher.connect();
    }

    public Mono<Optional<Course>> sendCourseRecord(Mono<Course> courseMono) {
        return courseMono.flatMap(course -> this.courseKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<String , Course>(this.topicName , course.getCourseId() , course) , 1)))
                .next()
                .doOnNext(log::info)
                .map(integerSenderResult -> integerSenderResult.exception() == null ? Optional.of(course) : Optional.empty()));
    }

}
