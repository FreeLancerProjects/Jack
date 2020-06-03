package com.endpoint.Jack.models;

import java.io.Serializable;

public class SocialMediaModel implements Serializable {

    private String company_facebook;
    private String company_twitter;
    private String company_instagram;
    private String company_telegram;
    private String company_snapchat;
    private String company_whatsapp;
    private String company_emails;
    private int network_per;

    public int getNetwork_per() {
        return network_per;
    }

    public String getCompany_facebook() {
        return company_facebook;
    }

    public String getCompany_twitter() {
        return company_twitter;
    }

    public String getCompany_instagram() {
        return company_instagram;
    }

    public String getCompany_telegram() {
        return company_telegram;
    }

    public String getCompany_snapchat() {
        return company_snapchat;
    }

    public String getCompany_whatsapp() {
        return company_whatsapp;
    }

    public String getCompany_emails() {
        return company_emails;
    }
}
