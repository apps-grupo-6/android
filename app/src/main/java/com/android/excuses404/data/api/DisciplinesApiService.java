package com.android.excuses404.data.api;

import com.android.excuses404.data.api.model.DisciplinesResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DisciplinesApiService {
    @GET("locations")
    Call<DisciplinesResponse> get_all_disciplines();
}
