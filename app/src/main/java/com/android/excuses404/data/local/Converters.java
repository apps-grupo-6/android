package com.android.excuses404.data.local;

import androidx.room.TypeConverter;
import com.android.excuses404.models.AttendanceStatus;

public class Converters {
    @TypeConverter
    public static String fromStatus(AttendanceStatus s) {
        return s == null ? null : s.name();
    }

    @TypeConverter
    public static AttendanceStatus toStatus(String s) {
        return s == null ? null : AttendanceStatus.valueOf(s);
    }
}
