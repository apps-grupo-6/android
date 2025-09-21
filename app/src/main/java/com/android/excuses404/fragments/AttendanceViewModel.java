package com.android.excuses404.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.android.excuses404.data.repository.AttendanceRepository;
import com.android.excuses404.models.Attendance;
import com.android.excuses404.models.ClassSession;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AttendanceViewModel extends ViewModel {
    private final AttendanceRepository repo;

    @Inject public AttendanceViewModel(AttendanceRepository repo){ this.repo = repo; }

    public LiveData<List<ClassSession>> sessions(){ return repo.observeSessions(); }
    public LiveData<List<Attendance>> myAttendance(){ return repo.observeMyAttendance(); }

    public void refresh(){ repo.refreshSessions(); }
    public void reserve(ClassSession s){ repo.reserve(s); }
    public void cancel(ClassSession s){ repo.cancel(s); }
    public void confirm(ClassSession s){ repo.confirm(s); }
}