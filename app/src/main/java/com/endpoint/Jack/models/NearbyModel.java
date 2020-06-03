package com.endpoint.Jack.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class NearbyModel implements Serializable {

    private String id;
    private String place_id;
    private String name;
    private Geometry geometry;
    private String icon;
    private float rating;
    private String vicinity;
    private Opening_Hours opening_hours;
    private double distance;
    private List<PhotosModel> photos;
    public List<PhotosModel> getPhotos() {
        return photos;
    }
    public Geometry getGeometry() {
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

    public Opening_Hours getOpening_hours() {
        return opening_hours;
    }

    public String getVicinity() {
        return vicinity;
    }

    public String getId() {
        return id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public class Geometry implements Serializable
    {
        private Location location;

        public Location getLocation() {
            return location;
        }
    }

    public class Location implements Serializable{

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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    public static Comparator<NearbyModel> distanceComparator = new Comparator<NearbyModel>() {
        @Override
        public int compare(NearbyModel jc1, NearbyModel jc2) {
            return (SphericalUtil.computeDistanceBetween(new LatLng(LocationModel.getLocation().getLatitude(),LocationModel.getLocation().getLongitude()),new LatLng(jc2.getGeometry().getLocation().getLat(),jc2.getGeometry().getLocation().getLng())) < SphericalUtil.computeDistanceBetween(new LatLng(LocationModel.getLocation().getLatitude(),LocationModel.getLocation().getLongitude()),new LatLng(jc1.getGeometry().getLocation().getLat(),jc1.getGeometry().getLocation().getLng())) ? 1 :
                    (SphericalUtil.computeDistanceBetween(new LatLng(LocationModel.getLocation().getLatitude(),LocationModel.getLocation().getLongitude()),new LatLng(jc2.getGeometry().getLocation().getLat(),jc2.getGeometry().getLocation().getLng())) == SphericalUtil.computeDistanceBetween(new LatLng(LocationModel.getLocation().getLatitude(),LocationModel.getLocation().getLongitude()),new LatLng(jc1.getGeometry().getLocation().getLat(),jc1.getGeometry().getLocation().getLng()))? 0 : -1));
        }
    };
}
