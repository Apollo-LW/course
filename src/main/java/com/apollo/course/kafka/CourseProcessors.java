package com.apollo.course.kafka;

import com.apollo.course.model.Course;
import com.apollo.course.model.CourseUser;
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
public class CourseProcessors {

    @Value("${course.kafka.store}")
    private String courseStateStoreName;
    @Value("${user.kafka.store}")
    private String courseUserStateStoreName;

    @Bean
    public Function<KStream<String, Course>, KTable<String, Course>> courseProcessor() {
        return courseKStream -> {
            courseKStream
                    .flatMap((courseId , course) -> course.getCourseMembers().stream().map(memberId -> new KeyValue<String, Course>(memberId , course)).collect(Collectors.toSet()))
                    .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.courseSerde()))
                    .aggregate(CourseUser::new , (memberId , course , courseUser) -> courseUser.addCourse(course) , Materialized.with(Serdes.String() , CustomSerdes.courseUserSerde()))
                    .toStream()
                    .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.courseUserSerde()))
                    .reduce((courseUser , updatedCourseUser) -> updatedCourseUser , Materialized.as(this.courseUserStateStoreName));
            return courseKStream
                    .groupByKey()
                    .reduce((course , updatedCourse) -> updatedCourse , Materialized.as(this.courseStateStoreName));
        };
    }
}
