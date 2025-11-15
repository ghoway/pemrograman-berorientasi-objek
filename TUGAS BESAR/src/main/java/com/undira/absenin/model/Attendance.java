package com.undira.absenin.model;

import java.time.LocalDateTime;

public class Attendance {
    private int id;
    private int studentId;
    private String studentName;
    private LocalDateTime attendanceTime;
    private String status;

    public Attendance() {}

    public Attendance(int id, int studentId, String studentName, LocalDateTime attendanceTime, String status) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.attendanceTime = attendanceTime;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public LocalDateTime getAttendanceTime() { return attendanceTime; }
    public void setAttendanceTime(LocalDateTime attendanceTime) { this.attendanceTime = attendanceTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}