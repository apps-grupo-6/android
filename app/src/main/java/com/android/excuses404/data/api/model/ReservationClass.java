package com.android.excuses404.data.api.model;

import com.google.gson.annotations.SerializedName;

public class ReservationClass {
    @SerializedName("class_id")
    private Integer classId;

    @SerializedName("class_scheduled_at")
    private String classScheduledAt;

    @SerializedName("participant_added_at")
    private String participantAddedAt;

    @SerializedName("participant_confirmed_at")
    private String participantConfirmedAt;

    @SerializedName("participant_status")
    private String participantStatus;

    @SerializedName("participant_updated_at")
    private String participantUpdatedAt;

    @SerializedName("professor_first_name")
    private String professorFirstName;

    @SerializedName("professor_last_name")
    private String professorLastName;

    public ReservationClass() {
    }

    public ReservationClass(Integer classId, String classScheduledAt, String participantAddedAt,
            String participantConfirmedAt, String participantStatus,
            String participantUpdatedAt, String professorFirstName, String professorLastName) {
        this.classId = classId;
        this.classScheduledAt = classScheduledAt;
        this.participantAddedAt = participantAddedAt;
        this.participantConfirmedAt = participantConfirmedAt;
        this.participantStatus = participantStatus;
        this.participantUpdatedAt = participantUpdatedAt;
        this.professorFirstName = professorFirstName;
        this.professorLastName = professorLastName;
    }

    public Integer getClassId() {
        return classId;
    }

    public String getClassScheduledAt() {
        return classScheduledAt;
    }

    public String getParticipantAddedAt() {
        return participantAddedAt;
    }

    public String getParticipantConfirmedAt() {
        return participantConfirmedAt;
    }

    public String getParticipantStatus() {
        return participantStatus;
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

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public void setClassScheduledAt(String classScheduledAt) {
        this.classScheduledAt = classScheduledAt;
    }

    public void setParticipantAddedAt(String participantAddedAt) {
        this.participantAddedAt = participantAddedAt;
    }

    public void setParticipantConfirmedAt(String participantConfirmedAt) {
        this.participantConfirmedAt = participantConfirmedAt;
    }

    public void setParticipantStatus(String participantStatus) {
        this.participantStatus = participantStatus;
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
        return "ReservationClass{" +
                "classId=" + classId +
                ", classScheduledAt='" + classScheduledAt + '\'' +
                ", participantAddedAt='" + participantAddedAt + '\'' +
                ", participantConfirmedAt='" + participantConfirmedAt + '\'' +
                ", participantStatus='" + participantStatus + '\'' +
                ", participantUpdatedAt='" + participantUpdatedAt + '\'' +
                ", professorFirstName='" + professorFirstName + '\'' +
                ", professorLastName='" + professorLastName + '\'' +
                '}';
    }
}
