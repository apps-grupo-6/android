package com.android.excuses404.data.repository;

import android.util.Log;

import com.android.excuses404.data.api.DisciplinesApiService;
import com.android.excuses404.data.api.model.DisciplinesResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class LocationsRepository {

    private final DisciplinesApiService apiService;

    @Inject
    public LocationsRepository(DisciplinesApiService apiService) {
        this.apiService = apiService;
    }

    public void getAllLocations(LocationsServiceCallBack callBack) {
        apiService.get_all_disciplines().enqueue(new Callback<DisciplinesResponse>() {
            @Override
            public void onResponse(Call<DisciplinesResponse> call, Response<DisciplinesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callBack.onSuccess(response.body());
                } else {
                    callBack.onError(new Exception("Error en la respuesta: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<DisciplinesResponse> call, Throwable t) {
                Log.e("LocationsRepository", "Error en petición", t);
                callBack.onError(t);
            }
        });
    }
}
