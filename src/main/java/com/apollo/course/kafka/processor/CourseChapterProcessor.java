package com.apollo.course.kafka.processor;

import com.apollo.course.kafka.CustomSerdes;
import com.apollo.course.model.Chapter;
import com.apollo.course.model.Course;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CourseChapterProcessor {

    @Value("${chapter.kafka.store}")
    private String courseChapterStateStoreName;

    @Bean
    public Function<KStream<String, Course>, KTable<String, Chapter>> courseChapter() {
        return courseKStream -> courseKStream
                .flatMap((courseId , course) -> course
                        .getCourseChapters()
                        .stream()
                        .map(chapter -> new KeyValue<String, Chapter>(chapter.getChapterId() , chapter)).collect(Collectors.toSet()))
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.chapterSerde()))
                .reduce((chapter , updatedChapter) -> updatedChapter , Materialized.as(this.courseChapterStateStoreName));
    }

}
