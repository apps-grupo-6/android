package com.android.excuses404.models;

public class Attendance {
    private final String id;
    private final String sessionId;
    private final int userId;
    private final AttendanceStatus status;
    private final long timestamp;

    public Attendance(String id, String sessionId, int userId, AttendanceStatus status, long timestamp) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getSessionId() { return sessionId; }
    public int getUserId() { return userId; }
    public AttendanceStatus getStatus() { return status; }
    public long getTimestamp() { return timestamp; }
}
