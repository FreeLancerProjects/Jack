package com.endpoint.Jack.models;

import java.io.Serializable;

public class SocialMediaModel implements Serializable {

    private String company_facebook;
    private String company_twitter;
    private String company_instagram;
    private String company_telegram;

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
}
