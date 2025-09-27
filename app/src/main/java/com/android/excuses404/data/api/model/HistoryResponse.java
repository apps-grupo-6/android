package com.android.excuses404.data.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HistoryResponse {
    @SerializedName("code")
    private String code;

    @SerializedName("description")
    private String description;

    @SerializedName("data")
    private List<HistoryClass> data;

    // Constructor vacío
    public HistoryResponse() {
    }

    // Constructor completo
    public HistoryResponse(String code, String description, List<HistoryClass> data) {
        this.code = code;
        this.description = description;
        this.data = data;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public List<HistoryClass> getData() {
        return data;
    }

    // Setters
    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setData(List<HistoryClass> data) {
        this.data = data;
    }

    // Método helper para verificar si la respuesta es exitosa
    public boolean isSuccess() {
        return "0200".equals(code);
    }

    @Override
    public String toString() {
        return "HistoryResponse{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", data=" + data +
                '}';
    }
}
