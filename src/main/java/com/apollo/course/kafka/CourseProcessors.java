package com.apollo.course.kafka;

import com.apollo.course.model.*;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class CourseProcessors {

    @Value("${course.kafka.store}")
    private String courseStateStoreName;
    @Value("${user.kafka.store}")
    private String courseUserStateStoreName;
    @Value("${chapter.kafka.store}")
    private String courseChapterStateStoreName;
    @Value("${course.kafka.enroll.store}")
    private String courseEnrollmentStateStoreName;

    @Bean
    public BiFunction<KStream<String, Course>, KStream<String, CourseEnrollmentRequest>, KTable<String, Course>> courseProcessor() {
        return (courseKStream, courseEnrollmentKStream) -> {
            courseKStream
                    .flatMap((courseId, course) -> {
                        Set<KeyValue<String, Course>> membersIds = course.getCourseMembers().stream().map(memberId -> new KeyValue<String, Course>(memberId, course)).collect(Collectors.toSet());
                        Set<KeyValue<String, Course>> ownersIds = course.getCourseOwners().stream().map(ownerId -> new KeyValue<String, Course>(ownerId, course)).collect(Collectors.toSet());
                        membersIds.addAll(ownersIds);
                        return membersIds;
                    })
                    .groupByKey(Grouped.with(Serdes.String(), CustomSerdes.courseSerde()))
                    .aggregate(CourseUser::new, (memberId, course, courseUser) -> courseUser.addCourse(course), Materialized.with(Serdes.String(), CustomSerdes.courseUserSerde()))
                    .toStream()
                    .groupByKey(Grouped.with(Serdes.String(), CustomSerdes.courseUserSerde()))
                    .reduce((courseUser, updatedCourseUser) -> updatedCourseUser, Materialized.as(this.courseUserStateStoreName));

            courseKStream
                    .flatMap((courseId, course) -> course.getCourseChapters().stream().map(chapter -> new KeyValue<String, Chapter>(chapter.getChapterId(), chapter)).collect(Collectors.toSet()))
                    .groupByKey(Grouped.with(Serdes.String(), CustomSerdes.chapterSerde()))
                    .reduce((chapter, updatedChapter) -> updatedChapter, Materialized.as(this.courseChapterStateStoreName));

            courseEnrollmentKStream
                    .groupByKey(Grouped.with(Serdes.String(), CustomSerdes.courseEnrollmentSerde()))
                    .aggregate(CourseEnrollment::new,
                            (courseId, courseEnrollment, courseEnrollmentA) -> courseEnrollmentA.addCourseEnrollment(courseEnrollment),
                            Materialized.with(Serdes.String(), CustomSerdes.courseEnrollmentRequestSerde()))
                    .toStream()
                    .groupByKey(Grouped.with(Serdes.String(), CustomSerdes.courseEnrollmentRequestSerde()))
                    .reduce((courseEnrollment, courseEnrollmentUpdate) -> courseEnrollmentUpdate, Materialized.as(this.courseEnrollmentStateStoreName));

            return courseKStream.groupByKey().reduce((course, updatedCourse) -> updatedCourse, Materialized.as(this.courseStateStoreName));
        };
    }
}
