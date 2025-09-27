package com.android.excuses404.data.api.model;

import com.google.gson.annotations.SerializedName;

public class HistoryClass {
    @SerializedName("gym_name")
    private String gymName;

    @SerializedName("gym_city")
    private String gymCity;

    @SerializedName("participant_updated_at")
    private String participantUpdatedAt;

    @SerializedName("professor_first_name")
    private String professorFirstName;

    @SerializedName("professor_last_name")
    private String professorLastName;

    public HistoryClass() {
    }

    public HistoryClass(String gymName, String gymCity, String participantUpdatedAt,
            String professorFirstName, String professorLastName) {
        this.gymName = gymName;
        this.gymCity = gymCity;
        this.participantUpdatedAt = participantUpdatedAt;
        this.professorFirstName = professorFirstName;
        this.professorLastName = professorLastName;
    }

    public String getGymName() {
        return gymName;
    }

    public String getGymCity() {
        return gymCity;
    }

    public String getParticipantUpdatedAt() {
        return participantUpdatedAt;
    }

    public String getProfessorFirstName() {
        return professorFirstName;
    }

    public String getProfessorLastName() {
        return professorLastName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public void setGymCity(String gymCity) {
        this.gymCity = gymCity;
    }

    public void setParticipantUpdatedAt(String participantUpdatedAt) {
        this.participantUpdatedAt = participantUpdatedAt;
    }

    public void setProfessorFirstName(String professorFirstName) {
        this.professorFirstName = professorFirstName;
    }

    public void setProfessorLastName(String professorLastName) {
        this.professorLastName = professorLastName;
    }

    public String getProfessorFullName() {
        if (professorFirstName != null && professorLastName != null) {
            return professorFirstName + " " + professorLastName;
        } else if (professorFirstName != null) {
            return professorFirstName;
        } else if (professorLastName != null) {
            return professorLastName;
        }
        return "Profesor no disponible";
    }

    @Override
    public String toString() {
        return "HistoryClass{" +
                "gymName='" + gymName + '\'' +
                ", gymCity='" + gymCity + '\'' +
                ", participantUpdatedAt='" + participantUpdatedAt + '\'' +
                ", professorFirstName='" + professorFirstName + '\'' +
                ", professorLastName='" + professorLastName + '\'' +
                '}';
    }
}
