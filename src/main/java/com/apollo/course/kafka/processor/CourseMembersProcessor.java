package com.apollo.course.kafka.processor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CourseMembersProcessor {

    @Value("${user.kafka.store}")
    private String courseUserStateStoreName;

/*
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
*/

}
