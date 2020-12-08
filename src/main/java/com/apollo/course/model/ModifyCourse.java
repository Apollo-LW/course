package com.apollo.course.model;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ModifyCourse {

    private String ownerId , courseId;
    private ArrayList<String> userIds = new ArrayList<>();

}
