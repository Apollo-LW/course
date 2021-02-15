package com.apollo.course.kafka.processor;

import com.apollo.course.model.Course;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CourseProcessor {

    @Value("${course.kafka.store}")
    private String courseStateStoreName;

    @Bean
    public Function<KStream<String, Course>, KTable<String, Course>> courseProcessorState() {
        return courseKStream -> courseKStream
                .groupByKey()
                .reduce((course , updatedCourse) -> updatedCourse , Materialized.as(this.courseStateStoreName));
    }

}
