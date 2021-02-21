package com.apollo.course.kafka.processor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CourseEnrollmentProcessor {

    @Value("${enroll.kafka.store}")
    private String courseEnrollmentStateStoreName;
/*
    @Bean
    public Function<KStream<String, CourseEnrollmentRequest>, KTable<String, CourseEnrollment>> courseEnrollment() {
        return courseEnrollmentRequestKStream -> courseEnrollmentRequestKStream
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.courseEnrollmentSerde()))
                .aggregate(CourseEnrollment::new ,
                        (courseEnrollmentId , courseEnrollmentRequest , courseEnrollment) -> courseEnrollment.addCourseEnrollment(courseEnrollmentRequest) ,
                        Materialized.with(Serdes.String() , CustomSerdes.courseEnrollmentRequestSerde()))
                .toStream()
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.courseEnrollmentRequestSerde()))
                .reduce((courseEnrollment , updateCourseEnrollment) -> updateCourseEnrollment , Materialized.as(this.courseEnrollmentStateStoreName));
    }*/

}
