package com.endpoint.Jack.models;

import java.io.Serializable;

public class QuerySearchModel implements Serializable {
    private String query;
    private int image_resource;

    public QuerySearchModel(String query, int image_resource) {
        this.query = query;
        this.image_resource = image_resource;
    }

    public String getQuery() {
        return query;
    }

    public int getImage_resource() {
        return image_resource;
    }
}
