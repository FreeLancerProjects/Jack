package com.endpoint.Jack.models;

import java.io.Serializable;
import java.util.List;

public class CategoryModel implements Serializable {
private List<Data>data;

    public List<Data> getData() {
        return data;
    }

    public class Data implements Serializable
        {
            private int category_id;
                private int parent_id;
                private int order_id;
                private String logo;
               private Word word;

            public int getCategory_id() {
                return category_id;
            }

            public int getParent_id() {
                return parent_id;
            }

            public int getOrder_id() {
                return order_id;
            }

            public String getLogo() {
                return logo;
            }

            public Word getWord() {
                return word;
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
        }

        }
