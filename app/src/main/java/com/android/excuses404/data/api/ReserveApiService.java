package com.android.excuses404.data.api;

import com.android.excuses404.data.api.model.ReserveResponse;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReserveApiService {
    @POST("classes/{classId}/participant")
    Call<ReserveResponse> reserveClass(
            @Path("classId") int classId,
            @Header("Authorization") String authorization);
}
