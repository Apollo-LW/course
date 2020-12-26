package com.apollo.course.model;

import lombok.Data;

@Data
public class Resource {

    private String resourceId , resourceUrl;
    private ResourceType resourceType;

}
