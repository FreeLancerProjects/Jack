package com.endpoint.Jack.models;

import java.io.Serializable;

public class NotificationTypeModel implements Serializable {

    private String notification_type;

    public NotificationTypeModel(String notification_type) {
        this.notification_type = notification_type;
    }

    public String getNotification_type() {
        return notification_type;
    }
}
