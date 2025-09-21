package com.android.excuses404.data.api.dto;
public class ParticipantUpdateReq {
    public String status;   // "CONFIRMED" o "PRESENT"
    public Integer userId;  // opcional si hay JWT
    public ParticipantUpdateReq(String status, Integer userId){
        this.status = status; this.userId = userId;
    }
}