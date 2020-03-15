package com.creativeshare.Jack.models;

import java.io.Serializable;
import java.util.List;

public class NearDelegateDataModel implements Serializable {

    private List<DelegateModel> data;
    private Meta meta;

    public List<DelegateModel> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public class DelegateModel implements Serializable
    {
        private String driver_id;
        private String user_phone;
        private String user_phone_code;
        private String user_full_name;
        private String user_image;
        private String user_google_lat;
        private String user_google_long;
        private double distance;
        private double rate;

        public String getDriver_id() {
            return driver_id;
        }

        public String getUser_phone() {
            return user_phone;
        }

        public String getUser_full_name() {
            return user_full_name;
        }

        public String getUser_image() {
            return user_image;
        }

        public String getUser_google_lat() {
            return user_google_lat;
        }

        public String getUser_google_long() {
            return user_google_long;
        }

        public double getDistance() {
            return distance;
        }

        public String getUser_phone_code() {
            return user_phone_code;
        }

        public double getRate() {
            return rate;
        }
    }

    public class Meta implements Serializable
    {
        private int current_page;
        private int last_page;
        private int total_drivers;


        public int getCurrent_page() {
            return current_page;
        }

        public int getLast_page() {
            return last_page;
        }

        public int getTotal_drivers() {
            return total_drivers;
        }
    }

}
