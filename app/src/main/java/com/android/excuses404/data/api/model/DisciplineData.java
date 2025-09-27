package com.android.excuses404.data.api.model;

import com.google.gson.annotations.SerializedName;

public class DisciplineData {
    @SerializedName("class_max_participants")
    private int classMaxParticipants;

    @SerializedName("class_scheduled_at")
    private String classScheduledAt;

    @SerializedName("discipline_name")
    private String disciplineName;

    @SerializedName("gym_name")
    private String gymName;

    @SerializedName("professor_name")
    private String professorName;

    // Getters
    public int getClassMaxParticipants() {
        return classMaxParticipants;
    }

    public String getClassScheduledAt() {
        return classScheduledAt;
    }

    public String getDisciplineName() {
        return disciplineName;
    }

    public String getGymName() {
        return gymName;
    }

    public String getProfessorName() {
        return professorName;
    }
}
