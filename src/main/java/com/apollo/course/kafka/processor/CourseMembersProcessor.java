package com.apollo.course.kafka.processor;

import com.apollo.course.kafka.CustomSerdes;
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
public class CourseMembersProcessor {

    @Value("${user.kafka.store}")
    private String courseUserStateStoreName;

    @Bean
    public Function<KStream<String, Course>, KTable<String, CourseUser>> courseMember() {
        return courseKStream -> courseKStream
                .flatMap((courseId , course) -> course
                        .getAllCourseMembers()
                        .stream()
                        .map(courseMember -> new KeyValue<String, Course>(courseMember , course)).collect(Collectors.toSet()))
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.courseSerde()))
                .aggregate(CourseUser::new ,
                        (memberId , course , courseUser) -> courseUser.addCourse(course) ,
                        Materialized.with(Serdes.String() , CustomSerdes.courseUserSerde()))
                .toStream()
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.courseUserSerde()))
                .reduce((courseUser , updatedCourseUser) -> updatedCourseUser , Materialized.as(this.courseUserStateStoreName));

    }

}
