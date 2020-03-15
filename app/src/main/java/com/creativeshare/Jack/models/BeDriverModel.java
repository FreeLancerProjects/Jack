package com.creativeshare.Jack.models;

import java.io.Serializable;

public class BeDriverModel implements Serializable {

    private String action_status;

    public BeDriverModel(String action_status) {
        this.action_status = action_status;
    }

    public String getAction_status() {
        return action_status;
    }
}
