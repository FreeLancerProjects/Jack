package com.endpoint.Jack.models;

import java.io.Serializable;
import java.util.List;

public class SingleCategoryModel implements Serializable {
private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public class Data implements Serializable
        {
            private int place_id;
                private String category_id;
                private String area_id;
                private String logo;
                private String address;
                private String google_lat;
                private String google_long;
                private List<Days>days;
                public class Days implements Serializable

            {
                private int day;
                    private String status;
                    private String from_time;
                    private String to_time;

                public int getDay() {
                    return day;
                }

                public String getStatus() {
                    return status;
                }

                public String getFrom_time() {
                    return from_time;
                }

                public String getTo_time() {
                    return to_time;
                }
            }


                private float rate;
                private Word word;
                private List<Menus> menus;

            public int getPlace_id() {
                return place_id;
            }

            public String getCategory_id() {
                return category_id;
            }

            public String getArea_id() {
                return area_id;
            }

            public String getLogo() {
                return logo;
            }

            public String getAddress() {
                return address;
            }

            public String getGoogle_lat() {
                return google_lat;
            }

            public String getGoogle_long() {
                return google_long;
            }

            public List<Days> getDays() {
                return days;
            }

            public float getRate() {
                return rate;
            }

            public Word getWord() {
                return word;
            }

            public List<Menus> getMenus() {
                return menus;
            }

            public class Word implements Serializable {
                private int id;

                private String title;
                private String content;

                public int getId() {
                    return id;
                }

                public String getTitle() {
                    return title;
                }

                public String getContent() {
                    return content;
                }

            }


            public class Menus implements Serializable
            {
                private int id;
                    private int place_id;
                    private String image;

                public int getId() {
                    return id;
                }

                public int getPlace_id() {
                    return place_id;
                }

                public String getImage() {
                    return image;
                }
            }

        }



}
