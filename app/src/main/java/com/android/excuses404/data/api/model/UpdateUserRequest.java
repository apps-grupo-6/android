package com.android.excuses404.data.api.model;

public class UpdateUserRequest {
    private String first_name;
    private String last_name;
    private String telephone;
    private String contact_email;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(String first_name, String last_name, String telephone, String contact_email) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.telephone = telephone;
        this.contact_email = contact_email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }
}
