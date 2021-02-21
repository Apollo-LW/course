package com.apollo.course.kafka.processor;

import com.apollo.course.kafka.serde.CustomSerdes;
import com.apollo.course.model.Chapter;
import com.apollo.course.model.Course;
import com.apollo.course.model.CourseChapter;
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
    public BiFunction<KStream<String, Course>, KStream<String, Chapter>, KTable<String, CourseChapter>> courseChapter() {
        return (courseKStream , chapterKStream) -> {
            final KTable<String, Course> courseKTable = courseKStream.flatMap((courseId , course) -> course
                    .getCourseChaptersIds()
                    .stream()
                    .map(chapterId -> new KeyValue<String, Course>(chapterId , course))
                    .collect(Collectors.toSet()))
                    .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.courseSerde()))
                    .reduce((course , updatedCourse) -> updatedCourse , Materialized.with(Serdes.String() , CustomSerdes.courseSerde()));
            final KTable<String, Chapter> chapterKTable = chapterKStream
                    .groupByKey()
                    .reduce((chapter , updatedChapter) -> updatedChapter , Materialized.with(Serdes.String() , CustomSerdes.chapterSerde()));

            return null;
        };
    }

}
