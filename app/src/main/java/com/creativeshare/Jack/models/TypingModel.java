package com.creativeshare.Jack.models;

import java.io.Serializable;

public class TypingModel implements Serializable {

    private String from_user_id;
    private String to_user_id;
    private String room_id;
    private String typing_value;
    private String from_name;

    public TypingModel(String from_user_id, String to_user_id, String room_id, String typing_value, String from_name) {
        this.from_user_id = from_user_id;
        this.to_user_id = to_user_id;
        this.room_id = room_id;
        this.typing_value = typing_value;
        this.from_name = from_name;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public String getTyping_value() {
        return typing_value;
    }

    public String getFrom_name() {
        return from_name;
    }
}
