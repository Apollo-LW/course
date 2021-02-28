package com.apollo.course.kafka.processor;

import com.apollo.course.kafka.serde.CustomSerdes;
import com.apollo.course.model.CourseEnrollment;
import com.apollo.course.model.CourseEnrollmentRequest;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class CourseEnrollmentProcessor {

    @Value("${enroll.kafka.store}")
    private String courseEnrollmentStateStoreName;

    @Bean
    public Function<KStream<String, CourseEnrollmentRequest>, KTable<String, CourseEnrollment>> courseEnrollment() {
        return courseEnrollmentRequestKStream -> courseEnrollmentRequestKStream
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.courseEnrollmentSerde()))
                .aggregate(CourseEnrollment::new ,
                        (courseEnrollmentId , courseEnrollmentRequest , courseEnrollment) -> courseEnrollment.addCourseEnrollmentRequest(courseEnrollmentRequest.getRequestId()) ,
                        Materialized.with(Serdes.String() , CustomSerdes.courseEnrollmentRequestSerde()))
                .toStream()
                .groupByKey(Grouped.with(Serdes.String() , CustomSerdes.courseEnrollmentRequestSerde()))
                .reduce((courseEnrollment , updateCourseEnrollment) -> updateCourseEnrollment , Materialized.as(this.courseEnrollmentStateStoreName));
    }

}
