package com.android.excuses404.data.api.model;

import com.android.excuses404.models.Class;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClassesResponse {
    @SerializedName("classes")
    private List<Class> classes;

    @SerializedName("message")
    private String message;

    @SerializedName("success")
    private boolean success;

    public ClassesResponse() {}

    public ClassesResponse(List<Class> classes, String message, boolean success) {
        this.classes = classes;
        this.message = message;
        this.success = success;
    }

    public List<Class> getClasses() { return classes; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }

    public void setClasses(List<Class> classes) { this.classes = classes; }
    public void setMessage(String message) { this.message = message; }
    public void setSuccess(boolean success) { this.success = success; }
}
