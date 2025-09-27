package com.android.excuses404.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Class implements Parcelable {
    @SerializedName("class_id")
    private String classId;

    @SerializedName("professor_first_name")
    private String professorFirstName;

    @SerializedName("gym_name")
    private String gymName;

    @SerializedName("gym_city")
    private String gymCity;

    @SerializedName("gym_address")
    private String gymAddress;

    @SerializedName("class_discipline_name")
    private String disciplineName;

    @SerializedName("class_scheduled_at")
    private String scheduledAt;

    @SerializedName("class_max_participants")
    private int maxParticipants;

    // Constructor vacío para Gson
    public Class() {}

    // Constructor con parámetros principales
    public Class(String classId, String professorFirstName,
                 String gymName, String gymCity, String disciplineName, String scheduledAt, int maxParticipants) {
        this.classId = classId;
        this.professorFirstName = professorFirstName;
        this.gymName = gymName;
        this.gymCity = gymCity;
        this.disciplineName = disciplineName;
        this.scheduledAt = scheduledAt;
        this.maxParticipants = maxParticipants;
    }

    // Getters
    public String getProfessorFirstName() { return professorFirstName; }
    public String getGymName() { return gymName; }
    public String getDisciplineName() { return disciplineName; }
    public String getScheduledAt() { return scheduledAt; }
    public int getMaxParticipants() { return maxParticipants; }

    // Setters
    public void setClassId(String classId) { this.classId = classId; }
    public void setProfessorFirstName(String professorFirstName) { this.professorFirstName = professorFirstName; }
    public void setGymName(String gymName) { this.gymName = gymName; }
    public void setGymCity(String gymCity) { this.gymCity = gymCity; }
    public void setGymAddress(String gymAddress) { this.gymAddress = gymAddress; }
    public void setDisciplineName(String disciplineName) { this.disciplineName = disciplineName; }
    public void setScheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    // Parcelable implementation
    protected Class(Parcel in) {
        classId = in.readString();
        professorFirstName = in.readString();
        gymName = in.readString();
        gymCity = in.readString();
        gymAddress = in.readString();
        disciplineName = in.readString();
        scheduledAt = in.readString();
        maxParticipants = in.readInt();
    }

    public static final Creator<Class> CREATOR = new Creator<Class>() {
        @Override
        public Class createFromParcel(Parcel in) {
            return new Class(in);
        }

        @Override
        public Class[] newArray(int size) {
            return new Class[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(classId);
        dest.writeString(professorFirstName);
        dest.writeString(gymName);
        dest.writeString(gymCity);
        dest.writeString(gymAddress);
        dest.writeString(disciplineName);
        dest.writeString(scheduledAt);
        dest.writeInt(maxParticipants);
    }
}
