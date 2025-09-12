package com.android.excuses404.data.api;

import com.android.excuses404.data.api.model.UserLoginRequest;
import com.android.excuses404.data.api.model.UserLoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApiService {
    @POST("auth/")
    Call<UserLoginResponse> login(@Body UserLoginRequest loginRequest);
}
