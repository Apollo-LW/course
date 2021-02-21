package com.apollo.course.kafka.service;

import com.apollo.course.model.Chapter;
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
public class KafkaChapterService {

    private final KafkaSender<String, Chapter> chapterKafkaSender;
    @Value("${chapter.kafka.topic}")
    private String chapterTopicName;

    public Mono<Optional<Chapter>> sendChapterRecord(final Mono<Chapter> chapterMono) {
        return chapterMono.flatMap(chapter -> this.chapterKafkaSender
                .send(Mono.just(SenderRecord.create(new ProducerRecord<String, Chapter>(this.chapterTopicName , chapter.getChapterId() , chapter) , chapter.getChapterId())))
                .next()
                .map(senderResult -> senderResult.exception() == null ? Optional.of(chapter) : Optional.empty()));
    }

}
