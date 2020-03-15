package com.endpoint.Jack.models;

import java.io.Serializable;
import java.util.List;

public class WatingOrderData implements Serializable {

    private List<WaitOrder> data;
    private Meta meta;

    public List<WaitOrder> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public class WaitOrder implements Serializable
    {
        private String order_id;
        private String driver_id;
        private String driver_user_image;
        private String driver_user_phone;
        private String driver_user_phone_code;
        private String driver_user_full_name;
        private String order_time_arrival;

        public String getOrder_id() {
            return order_id;
        }

        public String getDriver_id() {
            return driver_id;
        }

        public String getDriver_user_image() {
            return driver_user_image;
        }

        public String getDriver_user_phone() {
            return driver_user_phone;
        }

        public String getDriver_user_phone_code() {
            return driver_user_phone_code;
        }

        public String getDriver_user_full_name() {
            return driver_user_full_name;
        }

        public String getOrder_time_arrival() {
            return order_time_arrival;
        }
    }

    public class Meta implements Serializable
    {
        private int current_page;
        private int total_orders;
        public int getCurrent_page() {
            return current_page;
        }

        public int getTotal_orders() {
            return total_orders;
        }
    }

}
