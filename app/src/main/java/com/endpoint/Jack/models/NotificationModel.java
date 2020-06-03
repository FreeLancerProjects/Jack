package com.endpoint.Jack.models;

import java.io.Serializable;
import java.util.List;

public class NotificationModel implements Serializable {

    private String id_notification;
    private String date_notification;
    private String title_notification;
    private String from_user_phone;
    private String from_user_full_name;
    private String from_user_image;
    private String from_user_type;
    private String order_id;
    private String order_status;
    private String order_movement;
    private String driver_offer;
    private String order_details;
    private double rate;
    private String client_address;
    private String driver_id;
    private String client_id;
    private String place_lat;
    private String place_long;
    private String place_address;
    private String order_type;
    private String order_image;
    private List<Drivers> driver_list;

    public NotificationModel(String title_notification, String order_status) {
        this.title_notification = title_notification;
        this.order_status = order_status;
    }

    public String getId_notification() {
        return id_notification;
    }

    public String getDate_notification() {
        return date_notification;
    }

    public String getTitle_notification() {
        return title_notification;
    }

    public String getFrom_user_phone() {
        return from_user_phone;
    }

    public String getFrom_user_full_name() {
        return from_user_full_name;
    }

    public String getFrom_user_image() {
        return from_user_image;
    }

    public String getFrom_user_type() {
        return from_user_type;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getOrder_movement() {
        return order_movement;
    }

    public String getDriver_offer() {
        return driver_offer;
    }

    public String getOrder_details() {
        return order_details;
    }

    public double getRate() {
        return rate;
    }

    public String getClient_address() {
        return client_address;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getPlace_lat() {
        return place_lat;
    }

    public String getPlace_long() {
        return place_long;
    }

    public String getPlace_address() {
        return place_address;
    }

    public String getOrder_type() {
        return order_type;
    }

    public List<Drivers> getDriver_list() {
        return driver_list;
    }

    public String getOrder_image() {
        return order_image;
    }

    public class Drivers implements Serializable {
        private String id_notification;
        private String driver_id;
        private String date_notification;
        private String title_notification;
        private String order_status;
        private String order_id;
        private String driver_offer;
        private String user_phone_code;
        private String user_phone;
        private String user_full_name;
        private String user_image;
        private String user_type;
        private double distance;
        private double rate;
        private boolean certified;


        public String getId_notification() {
            return id_notification;
        }

        public String getDriver_id() {
            return driver_id;
        }

        public String getDate_notification() {
            return date_notification;
        }

        public String getTitle_notification() {
            return title_notification;
        }

        public String getOrder_status() {
            return order_status;
        }

        public String getOrder_id() {
            return order_id;
        }

        public String getDriver_offer() {
            return driver_offer;
        }

        public String getUser_phone_code() {
            return user_phone_code;
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

        public String getUser_type() {
            return user_type;
        }

        public double getDistance() {
            return distance;
        }

        public double getRate() {
            return rate;
        }

        public boolean isCertified() {
            return certified;
        }
    }
}
