package com.android.excuses404.data.api.dto;
public class JoinReq {
    public Integer userId; // si usás JWT, puede ir null
    public JoinReq(Integer userId){ this.userId = userId; }
}
