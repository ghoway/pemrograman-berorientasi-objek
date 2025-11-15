package com.undira.absenin.service;

import com.undira.absenin.dao.AttendanceDAO;
import com.undira.absenin.model.Attendance;
import com.undira.absenin.model.Schedule;
import com.undira.absenin.model.Student;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AttendanceService {
    private final AttendanceDAO attendanceDAO;
    private final StudentService studentService;
    private final ScheduleService scheduleService;

    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
        this.studentService = new StudentService();
        this.scheduleService = new ScheduleService();
    }

    public String processAttendance(String nim) throws SQLException {
        Student student = studentService.getStudentByNim(nim);
        if (student == null) {
            return "Error: Siswa dengan NIM " + nim + " tidak ditemukan.";
        }

        Schedule schedule = scheduleService.getScheduleForToday();
        if (schedule == null) {
            return "Error: Tidak ada jadwal absen untuk hari ini.";
        }

        LocalTime now = LocalTime.now();
        String status;

        if (now.isBefore(schedule.getTimeIn())) {
            status = "TEPAT WAKTU";
        } else if (now.isAfter(schedule.getTimeIn()) && now.isBefore(schedule.getTimeOut())) {
            status = "TERLAMBAT";
        } else {
            status = "PULANG AWAL";
        }
        
        List<Attendance> todayAttendances = attendanceDAO.findByDate(LocalDate.now());
        boolean alreadyAttended = todayAttendances.stream()
                .anyMatch(a -> a.getStudentId() == student.getId());
        
        if(alreadyAttended){
            return "Warning: " + student.getNama() + " sudah melakukan absen hari ini.";
        }

        Attendance attendance = new Attendance();
        attendance.setStudentId(student.getId());
        attendance.setAttendanceTime(LocalDateTime.now());
        attendance.setStatus(status);

        attendanceDAO.add(attendance);
        
        return "Success: " + student.getNama() + " berhasil absen dengan status " + status + ".";
    }
    
    public List<Attendance> getAttendancesForToday() throws SQLException {
        return attendanceDAO.findByDate(LocalDate.now());
    }
    
    public int getAttendanceCountForToday() throws SQLException {
        return attendanceDAO.countByDate(LocalDate.now());
    }
}