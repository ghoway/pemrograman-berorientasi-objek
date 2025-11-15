package com.undira.absenin.model;

import java.time.LocalTime;

public class Schedule {
    private int id;
    private String dayOfWeek;
    private LocalTime timeIn;
    private LocalTime timeOut;

    public Schedule() {}

    public Schedule(int id, String dayOfWeek, LocalTime timeIn, LocalTime timeOut) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getTimeIn() { return timeIn; }
    public void setTimeIn(LocalTime timeIn) { this.timeIn = timeIn; }
    public LocalTime getTimeOut() { return timeOut; }
    public void setTimeOut(LocalTime timeOut) { this.timeOut = timeOut; }
}