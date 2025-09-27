package com.android.excuses404.data.api;

import com.android.excuses404.data.api.model.HistoryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface HistoryApiService {
    @GET("classes/history")
    Call<HistoryResponse> getClassesHistory(@Header("Authorization") String authorization);
}
