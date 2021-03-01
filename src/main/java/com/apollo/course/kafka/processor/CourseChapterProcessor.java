package com.apollo.course.kafka.processor;

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

import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class CourseChapterProcessor {

    @Value("${chapter.kafka.store}")
    private String courseChapterStateStoreName;

    @Bean
    public BiFunction<KStream<String, Course>, KStream<String, Chapter>, KTable<String, Chapter>> courseChapter() {
        return (courseKStream , chapterKStream) -> {
            final KTable<String, String> courseKTable = courseKStream.flatMap((courseId , course) -> course
                    .getCourseChaptersIds()
                    .stream()
                    .map(chapterId -> new KeyValue<String, String>(chapterId , course.getCourseId()))
                    .collect(Collectors.toSet()))
                    .groupByKey(Grouped.with(Serdes.String() , Serdes.String()))
                    .reduce((course , updatedCourse) -> updatedCourse , Materialized.with(Serdes.String() , Serdes.String()));

            final KTable<String, String> chapterKTable = chapterKStream
                    .map((chapterId , chapter) -> new KeyValue<String, String>(chapterId , chapterId))
                    .groupByKey(Grouped.with(Serdes.String() , Serdes.String()))
                    .reduce((chapter , updatedChapter) -> updatedChapter , Materialized.with(Serdes.String() , Serdes.String()));

            return chapterKStream.groupByKey().reduce((chapter , updatedChapter) -> updatedChapter , Materialized.as(this.courseChapterStateStoreName));
        };
    }

}
