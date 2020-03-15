package com.creativeshare.Jack.models;

import java.io.Serializable;

public class AppDataModel implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public class Data
    {
        private String ar_content;
        private String en_content;

        public String getAr_content() {
            return ar_content;
        }

        public String getEn_content() {
            return en_content;
        }
    }
}
