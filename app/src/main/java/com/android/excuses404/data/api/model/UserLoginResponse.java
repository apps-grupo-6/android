package com.android.excuses404.data.api.model;

import com.google.gson.annotations.SerializedName;

public class UserLoginResponse {

    private String  code;
    private String description;
    private Integer userId;
    private String token;

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
