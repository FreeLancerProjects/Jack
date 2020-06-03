package com.endpoint.Jack.models;

import java.io.Serializable;

public class MessageModel implements Serializable {

    private String id_message;
    private String room_id_fk;
    private String date;
    private String message;
    private String message_type;
    private String file;
    private String from_user;
    private String from_user_full_name;
    private String from_user_image;
    private String from_user_phone_code;
    private String from_user_phone;
    private String to_user;
    private String to_user_full_name;
    private String to_user_image;
    private String to_user_phone_code;
    private String to_user_phone;
    private String bill_step;
    private String total_cost;
    public MessageModel(String id_message, String room_id_fk, String date, String message, String message_type, String file, String from_user, String from_user_full_name, String from_user_image, String from_user_phone_code, String from_user_phone, String to_user, String to_user_full_name, String to_user_image, String to_user_phone_code, String to_user_phone,String bill_step,String total_cost) {
        this.id_message = id_message;
        this.room_id_fk = room_id_fk;
        this.date = date;
        this.message = message;
        this.message_type = message_type;
        this.file = file;
        this.from_user = from_user;
        this.from_user_full_name = from_user_full_name;
        this.from_user_image = from_user_image;
        this.from_user_phone_code = from_user_phone_code;
        this.from_user_phone = from_user_phone;
        this.to_user = to_user;
        this.to_user_full_name = to_user_full_name;
        this.to_user_image = to_user_image;
        this.to_user_phone_code = to_user_phone_code;
        this.to_user_phone = to_user_phone;
        this.bill_step=bill_step;
        this.total_cost=total_cost;
    }

    public String getBill_step() {

        return bill_step;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public String getId_message() {
        return id_message;
    }

    public String getRoom_id_fk() {
        return room_id_fk;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getFrom_user() {
        return from_user;
    }

    public String getTo_user() {
        return to_user;
    }

    public String getFrom_user_full_name() {
        return from_user_full_name;
    }

    public String getFrom_user_image() {
        return from_user_image;
    }

    public String getFrom_user_phone_code() {
        return from_user_phone_code;
    }

    public String getFrom_user_phone() {
        return from_user_phone;
    }

    public String getTo_user_full_name() {
        return to_user_full_name;
    }

    public String getTo_user_phone_code() {
        return to_user_phone_code;
    }

    public String getTo_user_phone() {
        return to_user_phone;
    }

    public String getTo_user_image() {
        return to_user_image;
    }

    public void setId_message(String id_message) {
        this.id_message = id_message;
    }

    public void setRoom_id_fk(String room_id_fk) {
        this.room_id_fk = room_id_fk;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFrom_user(String from_user) {
        this.from_user = from_user;
    }

    public void setFrom_user_full_name(String from_user_full_name) {
        this.from_user_full_name = from_user_full_name;
    }

    public void setFrom_user_image(String from_user_image) {
        this.from_user_image = from_user_image;
    }

    public void setFrom_user_phone_code(String from_user_phone_code) {
        this.from_user_phone_code = from_user_phone_code;
    }

    public void setFrom_user_phone(String from_user_phone) {
        this.from_user_phone = from_user_phone;
    }

    public void setTo_user(String to_user) {
        this.to_user = to_user;
    }

    public void setTo_user_full_name(String to_user_full_name) {
        this.to_user_full_name = to_user_full_name;
    }

    public void setTo_user_image(String to_user_image) {
        this.to_user_image = to_user_image;
    }

    public void setTo_user_phone_code(String to_user_phone_code) {
        this.to_user_phone_code = to_user_phone_code;
    }

    public void setTo_user_phone(String to_user_phone) {
        this.to_user_phone = to_user_phone;
    }

    public String getMessage_type() {
        return message_type;
    }

    public String getFile() {
        return file;
    }
}
