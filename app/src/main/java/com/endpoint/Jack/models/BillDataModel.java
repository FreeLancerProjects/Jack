package com.endpoint.Jack.models;

import java.io.Serializable;

public class BillDataModel implements Serializable {


    private static String bill_step;
private static String totla_Cost;
    public static String getBill_step() {
        return bill_step;
    }

    public static void setBill_step(String bill_step) {
        BillDataModel.bill_step = bill_step;
    }

    public static String getTotla_Cost() {
        return totla_Cost;
    }

    public static void setTotla_Cost(String totla_Cost) {
        BillDataModel.totla_Cost = totla_Cost;
    }
}
