package com.android.excuses404.data.api;

import com.android.excuses404.data.api.dto.AttendanceDTO;
import com.android.excuses404.data.api.dto.ClassSessionDTO;
import com.android.excuses404.data.api.dto.CheckInReq;
import com.android.excuses404.data.api.dto.ConfirmReq;
import com.android.excuses404.data.api.dto.ReserveReq;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AttendanceApiService {
    @GET("sessions")
    Call<List<ClassSessionDTO>> fetchSessions();

    @POST("attendance/reserve")
    Call<AttendanceDTO> reserve(@Body ReserveReq body);

    @POST("attendance/confirm")
    Call<AttendanceDTO> confirm(@Body ConfirmReq body);

    @POST("attendance/checkin")
    Call<AttendanceDTO> checkIn(@Body CheckInReq body);
}
