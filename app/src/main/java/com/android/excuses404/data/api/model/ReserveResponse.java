package com.android.excuses404.data.api.model;

import com.google.gson.annotations.SerializedName;

public class ReserveResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("description")
    private String description;

    @SerializedName("data")
    private Object data; // Can be null or contain reservation details

    @SerializedName("request_id")
    private String requestId;

    // Constructor vacío
    public ReserveResponse() {
    }

    // Constructor completo
    public ReserveResponse(String code, String description, Object data, String requestId) {
        this.code = code;
        this.description = description;
        this.data = data;
        this.requestId = requestId;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Object getData() {
        return data;
    }

    public String getRequestId() {
        return requestId;
    }

    // Setters
    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    // Método helper para verificar si la respuesta es exitosa
    public boolean isSuccess() {
        return "0200".equals(code);
    }

    @Override
    public String toString() {
        return "ReserveResponse{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", data=" + data +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
