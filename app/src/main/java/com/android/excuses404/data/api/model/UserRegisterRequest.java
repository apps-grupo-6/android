package com.android.excuses404.data.api.model;

public class UserRegisterRequest {
    private String username;
    private String password;
    private String first_name;
    private String last_name;
    private String telephone;
    private String contact_email;

    public UserRegisterRequest(String username, String password, String firstName,
            String lastName, String telephone, String contactEmail) {
        this.username = username;
        this.password = password;
        this.first_name = firstName;
        this.last_name = lastName;
        this.telephone = telephone;
        this.contact_email = contactEmail;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
