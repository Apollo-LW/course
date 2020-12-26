package com.apollo.course.kafka;

import com.apollo.course.model.Chapter;
import com.apollo.course.model.Course;
import com.apollo.course.model.CourseUser;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.jetbrains.annotations.Contract;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class CustomSerdes {

    static public final class CourseSerde extends Serdes.WrapperSerde<Course> {
        public CourseSerde() {
            super(new JsonSerializer<>() , new JsonDeserializer<>(Course.class));
        }
    }

    static public final class CourseUserSerde extends Serdes.WrapperSerde<CourseUser> {
        public CourseUserSerde() {
            super(new JsonSerializer<>() , new JsonDeserializer<>(CourseUser.class));
        }
    }

    static public final class CourseChapterSerde extends Serdes.WrapperSerde<Chapter> {
        public CourseChapterSerde() {
            super(new JsonSerializer<>() , new JsonDeserializer<>(Chapter.class));
        }
    }

    @Contract(" -> new")
    public static Serde<Course> courseSerde() {
        return new CustomSerdes.CourseSerde();
    }

    @Contract(" -> new")
    public static Serde<CourseUser> courseUserSerde() {
        return new CustomSerdes.CourseUserSerde();
    }

    @Contract(" -> new")
    public static Serde<Chapter> chapterSerde() {
        return new CustomSerdes.CourseChapterSerde();
    }
}
