package com.android.excuses404.data.api.dto;

public class ReserveReq {
    public String sessionId;
    public int userId;

    public ReserveReq(String sessionId, int userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }
}
