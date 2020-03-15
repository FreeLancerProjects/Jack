package com.endpoint.Jack.models;

import java.io.Serializable;
import java.util.List;

public class CommentDataModel implements Serializable {

    private List<CommentModel> data;
    private Meta meta;

    public List<CommentModel> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public class CommentModel implements Serializable
    {
        private String client_comment;
        private String client_user_full_name;
        private String client_user_image;

        public String getClient_comment() {
            return client_comment;
        }

        public String getClient_user_full_name() {
            return client_user_full_name;
        }

        public String getClient_user_image() {
            return client_user_image;
        }
    }

    public class Meta implements Serializable
    {
        private int current_page;

        public int getCurrent_page() {
            return current_page;
        }
    }
}
