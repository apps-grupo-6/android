package com.android.excuses404.di;

import com.android.excuses404.data.api.ClassesApiService;
import com.android.excuses404.data.api.DisciplinesApiService;
import com.android.excuses404.data.api.HistoryApiService;
import com.android.excuses404.data.api.ReservationsApiService;
import com.android.excuses404.data.api.ReserveApiService;
import com.android.excuses404.data.api.UserApiService;
import com.android.excuses404.data.repository.LocationsRepository;
import com.android.excuses404.services.UserService;
import com.android.excuses404.services.UserServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    ClassesApiService provideClassesApiService(Retrofit retrofit) {
        return retrofit.create(ClassesApiService.class);
    }

    @Provides
    @Singleton
    public DisciplinesApiService provideDisciplinesApiService(Retrofit retrofit) {
        return retrofit.create(DisciplinesApiService.class);
    }

    @Provides
    @Singleton
    UserApiService provideUserApiService(Retrofit retrofit) {
        return retrofit.create(UserApiService.class);
    }

    @Provides
    @Singleton
    HistoryApiService provideHistoryApiService(Retrofit retrofit) {
        return retrofit.create(HistoryApiService.class);
    }

    @Provides
    @Singleton
    ReservationsApiService provideReservationsApiService(Retrofit retrofit) {
        return retrofit.create(ReservationsApiService.class);
    }

    @Provides
    @Singleton
    ReserveApiService provideReserveApiService(Retrofit retrofit) {
        return retrofit.create(ReserveApiService.class);
    }

    @Provides
    @Singleton
    UserService provideUserService(UserServiceImpl userServiceImpl) {
        return userServiceImpl;
    }
}
