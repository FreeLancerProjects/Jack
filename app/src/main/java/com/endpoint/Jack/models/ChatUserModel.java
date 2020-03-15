package com.endpoint.Jack.models;

import java.io.Serializable;

public class ChatUserModel implements Serializable {

    private String name;
    private String image;
    private String id;
    private String room_id;
    private String phone_code;
    private String phone;
    private String order_id;
    private String offer_cost;

    public ChatUserModel(String name, String image, String id, String room_id, String phone_code, String phone, String order_id, String offer_cost) {
        this.name = name;
        this.image = image;
        this.id = id;
        this.room_id = room_id;
        this.phone_code = phone_code;
        this.phone = phone;
        this.order_id = order_id;
        this.offer_cost = offer_cost;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public String getPhone() {
        return phone;
    }

    public String getOrder_id() {
        return order_id;
    }

    public String getOffer_cost() {
        return offer_cost;
    }
}
