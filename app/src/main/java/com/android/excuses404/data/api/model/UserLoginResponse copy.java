package com.android.excuses404.data.api.model;

import com.google.gson.annotations.SerializedName;

public class UserLoginResponse {

    private String code;
    private String description;
    private Data data;
    @SerializedName("request_id")
    private String requestId;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Data getData() {
        return data;
    }

    public String getRequestId() {
        return requestId;
    }

    public Integer getUserId() {
        return data != null ? data.getUserId() : null;
    }

    public String getToken() {
        return data != null ? data.getToken() : null;
    }

    public static class Data {
        private String token;
        @SerializedName("user_id")
        private Integer userId;

        public String getToken() {
            return token;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }
    }
}
