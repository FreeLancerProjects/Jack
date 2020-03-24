package com.endpoint.Jack.models;

import java.io.Serializable;
import java.util.List;

public class SearchModel implements Serializable {
    private String id;
    private String place_id;
    private String name;
    private NearbyModel.Geometry geometry;
    private String icon;
    private float rating;
    private String formatted_address;
    private Opening_Hours opening_hours;
    private List<PhotosModel> photos;



    public NearbyModel.Geometry getGeometry() {
        return geometry;
    }


    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public Opening_Hours getOpening_hours() {
        return opening_hours;
    }

    public String getId() {
        return id;
    }

    public List<PhotosModel> getPhotos() {
        return photos;
    }

    public String getPlace_id() {
        return place_id;
    }

    public class Geometry implements Serializable
    {
        private NearbyModel.Location location;

        public NearbyModel.Location getLocation() {
            return location;
        }
    }

    public class Location implements Serializable {

        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }

    public class Opening_Hours implements Serializable
    {
        private boolean open_now;

        public boolean isOpen_now() {
            return open_now;
        }
    }



}
