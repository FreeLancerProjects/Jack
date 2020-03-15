package com.creativeshare.Jack.models;

import java.io.Serializable;

public class NotStateModel implements Serializable {

    private String notification_state;

    public NotStateModel(String notification_state) {
        this.notification_state = notification_state;
    }

    public void setNotification_state(String notification_state) {
        this.notification_state = notification_state;
    }

    public String getNotification_state() {
        return notification_state;
    }

}
