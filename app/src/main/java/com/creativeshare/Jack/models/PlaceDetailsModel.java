package com.creativeshare.Jack.models;

import java.io.Serializable;
import java.util.List;

public class PlaceDetailsModel implements Serializable {

    private PlaceDetails result;

    public PlaceDetails getResult() {
        return result;
    }

    public class PlaceDetails implements Serializable
    {
        private Opening_Hours opening_hours;

        public Opening_Hours getOpening_hours() {
            return opening_hours;
        }


    }

    public class Opening_Hours implements Serializable
    {
        List<Periods> periods;
        List<String> weekday_text;
        public List<Periods> getPeriods() {
            return periods;
        }

        public List<String> getWeekday_text() {
            return weekday_text;
        }
    }

    public class Periods implements Serializable
    {
        private Open open;

        public Open getOpen() {
            return open;
        }
    }

    public class Open implements Serializable
    {
        private String time;

        public String getTime() {
            return time;
        }
    }
}
