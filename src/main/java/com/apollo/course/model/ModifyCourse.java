package com.apollo.course.model;

import lombok.Data;

import java.util.HashSet;

@Data
public class ModifyCourse {

    private String ownerId , courseId;
    private HashSet<String> userIds = new HashSet<>();

}
