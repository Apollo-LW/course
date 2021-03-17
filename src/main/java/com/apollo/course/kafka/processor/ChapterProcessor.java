package com.apollo.course.kafka.processor;

import com.apollo.course.model.Chapter;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ChapterProcessor {

    @Value("${chapter.kafka.store}")
    private String chapterStateStoreName;

    @Bean
    public Function<KStream<String, Chapter>, KTable<String, Chapter>> chapterProcessorState() {
        return chapterKStream -> chapterKStream
                .groupByKey()
                .reduce((chapter , updatedChapter) -> chapter , Materialized.as(this.chapterStateStoreName));
    }
}
