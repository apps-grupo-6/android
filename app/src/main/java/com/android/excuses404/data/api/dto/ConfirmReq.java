package com.android.excuses404.data.api.dto;

public class ConfirmReq {
    public String sessionId;
    public int userId;

    public ConfirmReq(String sessionId, int userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }
}
