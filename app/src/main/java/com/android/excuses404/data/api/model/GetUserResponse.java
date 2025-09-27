package com.android.excuses404.data.api.model;

import com.google.gson.annotations.SerializedName;

public class GetUserResponse {
    private String code;
    private String description;
    private UserData data;

    public GetUserResponse() {
    }

    public boolean isSuccess() {
        return "0200".equals(code);
    }

    public String getMessage() {
        return description;
    }

    public UserData getUser() {
        return data;
    }

    public static class UserData {
        private String first_name;
        private String last_name;
        private String telephone;
        private String contact_email;

        public UserData() {
        }

        public String getFirst_name() {
            return first_name;
        }

        public String getLast_name() {
            return last_name;
        }

        public String getTelephone() {
            return telephone;
        }

        public String getContact_email() {
            return contact_email;
        }
    }
}
