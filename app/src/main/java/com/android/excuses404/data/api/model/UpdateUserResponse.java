package com.android.excuses404.data.api.model;

public class UpdateUserResponse {
    private boolean success;
    private String message;

    public UpdateUserResponse() {
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
