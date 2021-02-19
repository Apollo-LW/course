package com.apollo.course.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Resource {

    private String resourceId, resourceUrl;
    private ResourceType resourceType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resource resource = (Resource) o;
        return Objects.equals(resourceId , resource.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId);
    }
}
