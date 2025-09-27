package com.android.excuses404.data.api.model;

public class ResendOtpRequest {
    private String username;
    private String type;

    public ResendOtpRequest(String username, String type) {
        this.username = username;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    public static final String TYPE_REGISTRATION = "REGISTRATION";
    public static final String TYPE_RECOVERY = "RECOVERY";
}
