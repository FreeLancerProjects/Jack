package com.endpoint.Jack.models;

import java.io.Serializable;
import java.util.List;

public class ReviewsCategoryModel {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public class Data implements Serializable{
         private float rate;
                 private String comment;

                 private long date;
                 private User user;

        public float getRate() {
            return rate;
        }

        public String getComment() {
            return comment;
        }

        public long getDate() {
            return date;
        }

        public User getUser() {
            return user;
        }

        public class User implements Serializable {
            private String user_id;
            private String user_phone;
            private String user_phone_code;
            private String user_type;
            private String user_full_name;
            private String user_email;
            private String user_image;
            private String user_address;
            private String user_token_id;
            private String user_country;
            private String user_gender;
            private String user_age;
            private String user_card_id;
            private String user_card_id_image;
            private String user_driving_license;
            private String available;

            private double rate;
            private int num_orders;
            private int num_comments;
            private double account_balance;
            private int  num_coupon;

            public String getUser_id() {
                return user_id;
            }

            public String getUser_phone() {
                return user_phone;
            }

            public String getUser_type() {
                return user_type;
            }

            public String getUser_full_name() {
                return user_full_name;
            }

            public String getUser_email() {
                return user_email;
            }

            public String getUser_image() {
                return user_image;
            }

            public String getUser_token_id() {
                return user_token_id;
            }

            public String getUser_country() {
                return user_country;
            }

            public String getUser_gender() {
                return user_gender;
            }

            public String getUser_age() {
                return user_age;
            }

            public String getUser_card_id() {
                return user_card_id;
            }

            public String getUser_card_id_image() {
                return user_card_id_image;
            }

            public String getUser_driving_license() {
                return user_driving_license;
            }

            public double getRate() {
                return rate;
            }

            public int getNum_orders() {
                return num_orders;
            }

            public int getNum_comments() {
                return num_comments;
            }

            public double getAccount_balance() {
                return account_balance;
            }

            public String getUser_phone_code() {
                return user_phone_code;
            }

            public String getUser_address() {
                return user_address;
            }

            public int getNum_coupon() {
                return num_coupon;
            }

            public String getAvailable() {
                return available;
            }
        }

    }
}
