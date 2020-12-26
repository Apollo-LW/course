package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class ShareCourse {

    private String ownerId , courseId;
    private HashSet<String> userIds = new HashSet<>();

}
