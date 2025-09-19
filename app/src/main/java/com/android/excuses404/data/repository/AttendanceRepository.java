package com.android.excuses404.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.android.excuses404.data.api.AttendanceApiService;
import com.android.excuses404.data.api.dto.*;
import com.android.excuses404.models.*;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class AttendanceRepository {

    private final AttendanceApiService api;
    private final MutableLiveData<List<ClassSession>> sessions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Attendance>> myAttendance = new MutableLiveData<>(new ArrayList<>());

    @Inject
    public AttendanceRepository(AttendanceApiService api) { this.api = api; }

    public LiveData<List<ClassSession>> observeSessions() { return sessions; }
    public LiveData<List<Attendance>> observeMyAttendance() { return myAttendance; }

    public void refreshSessions() {
        api.fetchSessions().enqueue(new Callback<List<ClassSessionDTO>>() {
            @Override public void onResponse(Call<List<ClassSessionDTO>> c, Response<List<ClassSessionDTO>> r) {
                if (!r.isSuccessful() || r.body()==null) return;
                List<ClassSession> mapped = new ArrayList<>();
                for (ClassSessionDTO d : r.body()) {
                    mapped.add(new ClassSession(d.id, d.title, d.startsAt, d.endsAt, d.coach, d.capacity, d.reserved, d.confirmed));
                }
                sessions.postValue(mapped);
            }
            @Override public void onFailure(Call<List<ClassSessionDTO>> c, Throwable t) { /* log/notify */ }
        });
    }

    public void reserve(ClassSession s, int userId) {
        api.reserve(new ReserveReq(s.getId(), userId)).enqueue(new Callback<AttendanceDTO>() {
            @Override public void onResponse(Call<AttendanceDTO> c, Response<AttendanceDTO> r) {
                if (!r.isSuccessful() || r.body()==null) return;

                // update sessions (reserved = true)
                List<ClassSession> list = new ArrayList<>(sessions.getValue());
                for (int i=0;i<list.size();i++) if (list.get(i).getId().equals(s.getId())) {
                    list.set(i, s.withReserved(true));
                    break;
                }
                sessions.postValue(list);

                // push to myAttendance
                AttendanceDTO dto = r.body();
                List<Attendance> att = new ArrayList<>(myAttendance.getValue());
                att.add(new Attendance(dto.id, dto.sessionId, dto.userId,
                        AttendanceStatus.valueOf(dto.status), dto.timestamp));
                myAttendance.postValue(att);
            }
            @Override public void onFailure(Call<AttendanceDTO> c, Throwable t) { }
        });
    }

    public void confirm(ClassSession s, int userId) {
        api.confirm(new ConfirmReq(s.getId(), userId)).enqueue(new Callback<AttendanceDTO>() {
            @Override public void onResponse(Call<AttendanceDTO> c, Response<AttendanceDTO> r) {
                if (!r.isSuccessful() || r.body()==null) return;
                List<ClassSession> list = new ArrayList<>(sessions.getValue());
                for (int i=0;i<list.size();i++) if (list.get(i).getId().equals(s.getId())) {
                    list.set(i, s.withConfirmed(true));
                    break;
                }
                sessions.postValue(list);
            }
            @Override public void onFailure(Call<AttendanceDTO> c, Throwable t) { }
        });
    }

    public void checkIn(ClassSession s, int userId) {
        api.checkIn(new CheckInReq(s.getId(), userId)).enqueue(new Callback<AttendanceDTO>() {
            @Override public void onResponse(Call<AttendanceDTO> c, Response<AttendanceDTO> r) {
                if (!r.isSuccessful() || r.body()==null) return;
                AttendanceDTO dto = r.body();
                List<Attendance> att = new ArrayList<>(myAttendance.getValue());
                att.add(new Attendance(dto.id, dto.sessionId, dto.userId,
                        AttendanceStatus.PRESENT, dto.timestamp));
                myAttendance.postValue(att);
            }
            @Override public void onFailure(Call<AttendanceDTO> c, Throwable t) { }
        });
    }
}
