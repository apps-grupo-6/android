package com.android.excuses404.data.api;

import android.util.Log;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String TAG = "ApiClient";
    // Para emulador Android usa 10.0.2.2, para dispositivo físico usa la IP de tu PC
    private static final String BASE_URL = "http://10.0.2.2:5000/";
    // Si usas dispositivo físico, cambia por: "http://192.168.0.15:5000/"

    public static ClassesApiService getApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)  // Más tiempo para conectar
                .readTimeout(30, TimeUnit.SECONDS)     // Más tiempo para leer
                .writeTimeout(30, TimeUnit.SECONDS)    // Más tiempo para escribir
                .build();

        try {
            Log.d(TAG, "Conectando a: " + BASE_URL);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            return retrofit.create(ClassesApiService.class);
        } catch (Exception e) {
            Log.e(TAG, "Error creando el cliente Retrofit: " + e.getMessage());
            throw e;
        }
    }
}