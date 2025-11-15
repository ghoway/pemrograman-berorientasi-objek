package com.undira.absenin.dao;

import com.undira.absenin.model.Attendance;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public void add(Attendance attendance) throws SQLException {
        String sql = "INSERT INTO attendances (student_id, attendance_time, status) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, attendance.getStudentId());
            pstmt.setTimestamp(2, Timestamp.valueOf(attendance.getAttendanceTime()));
            pstmt.setString(3, attendance.getStatus());
            pstmt.executeUpdate();
        }
    }

    public List<Attendance> findByDate(LocalDate date) throws SQLException {
        List<Attendance> attendances = new ArrayList<>();
        String sql = "SELECT a.id, a.student_id, s.nama, a.attendance_time, a.status " +
                     "FROM attendances a JOIN students s ON a.student_id = s.id " +
                     "WHERE DATE(a.attendance_time) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                attendances.add(mapResultSetToAttendance(rs));
            }
        }
        return attendances;
    }
    
    public int countByDate(LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT student_id) FROM attendances WHERE DATE(attendance_time) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Attendance mapResultSetToAttendance(ResultSet rs) throws SQLException {
        return new Attendance(
            rs.getInt("id"),
            rs.getInt("student_id"),
            rs.getString("nama"),
            rs.getTimestamp("attendance_time").toLocalDateTime(),
            rs.getString("status")
        );
    }
}