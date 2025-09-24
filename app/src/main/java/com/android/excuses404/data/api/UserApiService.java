package com.android.excuses404.data.api;

import com.android.excuses404.data.api.model.OtpVerificationRequest;
import com.android.excuses404.data.api.model.OtpVerificationResponse;
import com.android.excuses404.data.api.model.ResendOtpRequest;
import com.android.excuses404.data.api.model.ResendOtpResponse;
import com.android.excuses404.data.api.model.ResetPasswordRequest;
import com.android.excuses404.data.api.model.ResetPasswordResponse;
import com.android.excuses404.data.api.model.UserLoginRequest;
import com.android.excuses404.data.api.model.UserLoginResponse;
import com.android.excuses404.data.api.model.UserRegisterRequest;
import com.android.excuses404.data.api.model.UserRegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApiService {
    @POST("auth/")
    Call<UserLoginResponse> login(@Body UserLoginRequest loginRequest);

    @POST("users/")
    Call<UserRegisterResponse> register(@Body UserRegisterRequest registerRequest);

    @POST("auth/verify")
    Call<OtpVerificationResponse> verifyOtp(@Body OtpVerificationRequest otpRequest);

    @POST("auth/resend-otp")
    Call<ResendOtpResponse> resendOtp(@Body ResendOtpRequest resendRequest);

    @POST("auth/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest resetRequest);
}
