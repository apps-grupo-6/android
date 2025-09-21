package com.android.excuses404.data.api;

import com.android.excuses404.data.api.dto.AttendanceDTO;
import com.android.excuses404.data.api.dto.ClassSessionDTO;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AttendanceApiService {

    // [GET] /classes/upcoming  → trae futuras clases del usuario logueado (según JWT)
    @GET("classes/upcoming")
    Call<List<ClassSessionDTO>> fetchUpcomingClasses();

    // [POST] /classes/{id}/participant  → reservar / unirse a la clase
    @POST("classes/{id}/participant")
    Call<AttendanceDTO> reserve(@Path("id") String classId);

    // [DELETE] /classes/{id}/participant → cancelar reserva
    @DELETE("classes/{id}/participant")
    Call<Void> cancel(@Path("id") String classId);

    // [PUT] /classes/{id}/participant/confirm → confirmar asistencia
    @PUT("classes/{id}/participant/confirm")
    Call<AttendanceDTO> confirm(@Path("id") String classId);
}
