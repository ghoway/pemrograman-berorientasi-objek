package com.undira.absenin.dao;

import com.undira.absenin.model.Schedule;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {

    public void add(Schedule schedule) throws SQLException {
        String sql = "INSERT INTO schedules (day_of_week, time_in, time_out) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, schedule.getDayOfWeek());
            pstmt.setTime(2, Time.valueOf(schedule.getTimeIn()));
            pstmt.setTime(3, Time.valueOf(schedule.getTimeOut()));
            pstmt.executeUpdate();
        }
    }

    public void update(Schedule schedule) throws SQLException {
        String sql = "UPDATE schedules SET day_of_week = ?, time_in = ?, time_out = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, schedule.getDayOfWeek());
            pstmt.setTime(2, Time.valueOf(schedule.getTimeIn()));
            pstmt.setTime(3, Time.valueOf(schedule.getTimeOut()));
            pstmt.setInt(4, schedule.getId());
            pstmt.executeUpdate();
        }
    }
    
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM schedules WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Schedule> findAll() throws SQLException {
        List<Schedule> schedules = new ArrayList<>();
        String sql = "SELECT * FROM schedules ORDER BY FIELD(day_of_week, 'Senin', 'Selasa', 'Rabu', 'Kamis', 'Jumat', 'Sabtu', 'Minggu')";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                schedules.add(mapResultSetToSchedule(rs));
            }
        }
        return schedules;
    }
    
    public Schedule findByDay(String day) throws SQLException {
        String sql = "SELECT * FROM schedules WHERE day_of_week = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, day);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSchedule(rs);
            }
        }
        return null;
    }

    private Schedule mapResultSetToSchedule(ResultSet rs) throws SQLException {
        return new Schedule(
            rs.getInt("id"),
            rs.getString("day_of_week"),
            rs.getTime("time_in").toLocalTime(),
            rs.getTime("time_out").toLocalTime()
        );
    }
}