package com.creativeshare.Jack.models;

import java.io.Serializable;

public class FollowModel implements Serializable {

    private String client_lat;
    private String client_long;
    private String place_lat;
    private String place_long;
    private String driver_lat;
    private String driver_long;


    public FollowModel(String client_lat, String client_long, String place_lat, String place_long, String driver_lat, String driver_long) {
        this.client_lat = client_lat;
        this.client_long = client_long;
        this.place_lat = place_lat;
        this.place_long = place_long;
        this.driver_lat = driver_lat;
        this.driver_long = driver_long;
    }

    public String getClient_lat() {
        return client_lat;
    }

    public String getClient_long() {
        return client_long;
    }

    public String getPlace_lat() {
        return place_lat;
    }

    public String getPlace_long() {
        return place_long;
    }

    public String getDriver_lat() {
        return driver_lat;
    }

    public String getDriver_long() {
        return driver_long;
    }

    public double getClientLat()
    {
        return Double.parseDouble(getClient_lat());
    }

    public double getClientLng()
    {
        return Double.parseDouble(getClient_long());
    }

    public double getPlaceLat()
    {
        return Double.parseDouble(getPlace_lat());
    }
    public double getPlaceLng()
    {
        return Double.parseDouble(getPlace_long());
    }

    public double getDriverLat()
    {
        return Double.parseDouble(getDriver_lat());
    }
    public double getDriverLng()
    {
        return Double.parseDouble(getDriver_long());
    }
}
