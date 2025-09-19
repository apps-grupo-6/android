package com.android.excuses404.models;

public class ClassSession {
    private final String id;
    private final String title;
    private final long startsAt;
    private final long endsAt;
    private final String coach;
    private final int capacity;
    private final boolean reserved;
    private final boolean confirmed;

    public ClassSession(String id, String title, long startsAt, long endsAt,
                        String coach, int capacity, boolean reserved, boolean confirmed) {
        this.id = id;
        this.title = title;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.coach = coach;
        this.capacity = capacity;
        this.reserved = reserved;
        this.confirmed = confirmed;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public long getStartsAt() { return startsAt; }
    public long getEndsAt() { return endsAt; }
    public String getCoach() { return coach; }
    public int getCapacity() { return capacity; }
    public boolean isReserved() { return reserved; }
    public boolean isConfirmed() { return confirmed; }

    public ClassSession withReserved(boolean value) {
        return new ClassSession(id, title, startsAt, endsAt, coach, capacity, value, confirmed);
    }
    public ClassSession withConfirmed(boolean value) {
        return new ClassSession(id, title, startsAt, endsAt, coach, capacity, reserved, value);
    }
}
