package com.android.excuses404.data.api.dto;

public class CheckInReq {
    public String sessionId;
    public int userId;

    public CheckInReq(String sessionId, int userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }
}
