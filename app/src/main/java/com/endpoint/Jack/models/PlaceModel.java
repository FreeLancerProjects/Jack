package com.endpoint.Jack.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class PlaceModel implements Serializable {
    private String id;
    private String place_id;
    private String name;
    private String icon;
    private float rating;
    private double lat;
    private double lng;
    private String address;
    private boolean isOpenNow;
    private List<String> weekday_text;
    private List<PhotosModel> photosList;
    public static Comparator<PlaceModel> distanceComparator = new Comparator<PlaceModel>() {
        @Override
        public int compare(PlaceModel jc1, PlaceModel jc2) {
            return (SphericalUtil.computeDistanceBetween(new LatLng(LocationModel.getLocation().getLatitude(),LocationModel.getLocation().getLongitude()),new LatLng(jc2.getLat(),jc2.getLng())) < SphericalUtil.computeDistanceBetween(new LatLng(LocationModel.getLocation().getLatitude(),LocationModel.getLocation().getLongitude()),new LatLng(jc1.getLat(),jc1.getLng())) ? 1 :
                    (SphericalUtil.computeDistanceBetween(new LatLng(LocationModel.getLocation().getLatitude(),LocationModel.getLocation().getLongitude()),new LatLng(jc2.getLat(),jc2.getLng())) == SphericalUtil.computeDistanceBetween(new LatLng(LocationModel.getLocation().getLatitude(),LocationModel.getLocation().getLongitude()),new LatLng(jc1.getLat(),jc1.getLng()))? 0 : -1));
        }
    };
    public PlaceModel(String id, String place_id, String name, String icon, List<PhotosModel> photosList, float rating, double lat, double lng, String address) {
        this.id = id;
        this.place_id = place_id;
        this.name = name;
        this.icon = icon;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.photosList = photosList;
    }

    public String getId() {
        return id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public float getRating() {
        return rating;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public boolean isOpenNow() {
        return isOpenNow;
    }

    public List<String> getWeekday_text() {
        return weekday_text;
    }

    public void setOpenNow(boolean openNow) {
        isOpenNow = openNow;
    }

    public void setWeekday_text(List<String> weekday_text) {
        this.weekday_text = weekday_text;
    }

    public String getAddress() {
        return address;
    }

    public List<PhotosModel> getPhotosList() {
        return photosList;
    }
}
