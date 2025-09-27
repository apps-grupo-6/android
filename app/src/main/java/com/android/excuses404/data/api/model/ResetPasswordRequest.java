package com.android.excuses404.data.api.model;

public class ResetPasswordRequest {
    private String reset_token;
    private String new_password;

    public ResetPasswordRequest(String resetToken, String newPassword) {
        this.reset_token = resetToken;
        this.new_password = newPassword;
    }

    public String getReset_token() {
        return reset_token;
    }

    public String getNew_password() {
        return new_password;
    }
}
