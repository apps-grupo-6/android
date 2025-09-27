package com.android.excuses404.data.repository;

import android.util.Log;

import com.android.excuses404.data.api.UserApiService;
import com.android.excuses404.data.api.model.UpdateUserRequest;
import com.android.excuses404.data.api.model.UpdateUserResponse;
import com.android.excuses404.data.api.model.GetUserResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

@Singleton
public class UserRepository {
    private static final String TAG = "UserRepository";
    private final UserApiService userApiService;

    @Inject
    public UserRepository(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    public void updateUser(String token, UpdateUserRequest updateRequest, UserServiceCallBack callBack) {
        String authHeader = "Bearer " + token;
        Call<ResponseBody> call = userApiService.updateUser(authHeader, updateRequest);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();

                        JsonParser parser = new JsonParser();
                        JsonObject jsonResponse = parser.parse(responseString).getAsJsonObject();
                        String code = jsonResponse.get("code").getAsString();
                        String description = jsonResponse.get("description").getAsString();

                        UpdateUserResponse updateResponse = new UpdateUserResponse();
                        updateResponse.setSuccess("0200".equals(code));
                        updateResponse.setMessage(description);

                        if ("0200".equals(code)) {
                            Log.d(TAG, "Usuario actualizado correctamente");
                            callBack.onSuccess(updateResponse);
                        } else {
                            Log.e(TAG, "Error al actualizar usuario: " + description);
                            callBack.onError(description);
                        }
                    } catch (Exception e) {
                        String errorMsg = "Error al parsear respuesta: " + e.getMessage();
                        Log.e(TAG, errorMsg, e);
                        callBack.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "Error en la respuesta del servidor: " + response.code();
                    Log.e(TAG, errorMsg);
                    callBack.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callBack.onError(errorMsg);
            }
        });
    }

    public void getUser(String token, UserServiceCallBack callBack) {
        String authHeader = "Bearer " + token;
        Call<GetUserResponse> call = userApiService.getUser(authHeader);

        call.enqueue(new Callback<GetUserResponse>() {
            @Override
            public void onResponse(Call<GetUserResponse> call, Response<GetUserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetUserResponse getUserResponse = response.body();
                    if (getUserResponse.isSuccess()) {
                        Log.d(TAG, "Datos del usuario obtenidos correctamente");
                        callBack.onSuccess(getUserResponse);
                    } else {
                        Log.e(TAG, "Error al obtener datos del usuario: " + getUserResponse.getMessage());
                        callBack.onError(getUserResponse.getMessage());
                    }
                } else {
                    String errorMsg = "Error en la respuesta del servidor: " + response.code();
                    Log.e(TAG, errorMsg);
                    callBack.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<GetUserResponse> call, Throwable t) {
                String errorMsg = "Error de conexión: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callBack.onError(errorMsg);
            }
        });
    }
}
