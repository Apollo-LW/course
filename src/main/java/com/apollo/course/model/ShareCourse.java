package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ShareCourse {

    private final String ownerId, courseId;
    private final Set<String> userIds = new HashSet<>();

}
