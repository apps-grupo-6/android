package com.android.excuses404.data.api;

import com.android.excuses404.data.api.model.ConfirmAccountRequest;
import com.android.excuses404.data.api.model.ConfirmAccountResponse;
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
import com.android.excuses404.data.api.model.UpdateUserRequest;
import com.android.excuses404.data.api.model.UpdateUserResponse;
import com.android.excuses404.data.api.model.GetUserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import okhttp3.ResponseBody;

public interface UserApiService {
    @POST("auth/")
    Call<UserLoginResponse> login(@Body UserLoginRequest loginRequest);

    @POST("auth/confirmAccount")
    Call<ConfirmAccountResponse> confirmAccount(@Body ConfirmAccountRequest confirmRequest);

    @POST("auth/verify")
    Call<OtpVerificationResponse> verifyOtp(@Body OtpVerificationRequest otpRequest);

    @POST("auth/resend-otp")
    Call<ResendOtpResponse> resendOtp(@Body ResendOtpRequest resendRequest);

    @POST("auth/reset-password")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest resetRequest);

    @POST("users/")
    Call<UserRegisterResponse> register(@Body UserRegisterRequest registerRequest);

    @PUT("users/")
    Call<ResponseBody> updateUser(@Header("Authorization") String authorization,
            @Body UpdateUserRequest updateRequest);

    @GET("users/")
    Call<GetUserResponse> getUser(@Header("Authorization") String authorization);
}
