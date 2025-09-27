package com.android.excuses404.data.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DisciplinesResponse {

    @SerializedName("data")
    private List<DisciplineData> data;

    @SerializedName("request_id")
    private String requestId;

    public List<DisciplineData> getData() {
        return data;
    }

    public String getRequestId() {
        return requestId;
    }
}
