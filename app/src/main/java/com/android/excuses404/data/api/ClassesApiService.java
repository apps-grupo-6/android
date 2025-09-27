package com.android.excuses404.data.api;

import com.android.excuses404.data.api.model.ClassesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ClassesApiService {
    @GET("api/classes/")
    Call<ClassesResponse> getAllClasses(@Header("Authorization") String token);
}
