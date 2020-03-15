package com.endpoint.Jack.models;

import java.io.Serializable;
import java.util.List;

public class SliderModel implements Serializable {

    private List<SliderImage> data;

    public List<SliderImage> getData() {
        return data;
    }

    public class SliderImage implements Serializable
    {
        private String image;

        public String getImage() {
            return image;
        }
    }

}
