package com.android.excuses404.data.api.model;

public class OtpVerificationResponse {
    private String code;
    private String description;
    private String message;
    private String reset_token;
    private OtpVerificationData data;

    public OtpVerificationResponse(String code, String description, String message) {
        this.code = code;
        this.description = description;
        this.message = message;
    }

    public OtpVerificationResponse(String code, String description, String message, String resetToken) {
        this.code = code;
        this.description = description;
        this.message = message;
        this.reset_token = resetToken;
    }

    public static class OtpVerificationData {
        private String message;
        private String reset_token;
        private String type;
        private String username;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getReset_token() {
            return reset_token;
        }

        public void setReset_token(String reset_token) {
            this.reset_token = reset_token;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReset_token() {
        if (data != null && data.getReset_token() != null) {
            return data.getReset_token();
        }
        return reset_token;
    }

    public void setReset_token(String reset_token) {
        this.reset_token = reset_token;
    }

    public OtpVerificationData getData() {
        return data;
    }

    public void setData(OtpVerificationData data) {
        this.data = data;
    }
}
