package com.android.excuses404.data.api.dto;

public class AttendanceDTO {
    public String id;
    public String sessionId;
    public int userId;         // uso int porque tu User usa int
    public String status;      // "RESERVED" | "CONFIRMED" | "PRESENT"
    public long timestamp;
}
