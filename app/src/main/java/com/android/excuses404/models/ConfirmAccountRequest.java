package com.android.excuses404.data.api.model;

public class ConfirmAccountRequest {
    private String username;
    private String verification_code;

    public ConfirmAccountRequest(String username, String verificationCode) {
        this.username = username;
        this.verification_code = verificationCode;
    }

    public String getUsername() {
        return username;
    }

    public String getVerification_code() {
        return verification_code;
    }
}
