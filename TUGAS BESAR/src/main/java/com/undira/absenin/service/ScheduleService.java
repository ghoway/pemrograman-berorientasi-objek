package com.undira.absenin.service;

import com.undira.absenin.dao.ScheduleDAO;
import com.undira.absenin.model.Schedule;
import java.sql.SQLException;
import java.util.List;

public class ScheduleService {
    private final ScheduleDAO scheduleDAO;

    public ScheduleService() {
        this.scheduleDAO = new ScheduleDAO();
    }
    
    public void addSchedule(Schedule schedule) throws SQLException {
        scheduleDAO.add(schedule);
    }

    public void updateSchedule(Schedule schedule) throws SQLException {
        scheduleDAO.update(schedule);
    }
    
    public void deleteSchedule(int id) throws SQLException {
        scheduleDAO.delete(id);
    }

    public List<Schedule> getAllSchedules() throws SQLException {
        return scheduleDAO.findAll();
    }
    
    public Schedule getScheduleForToday() throws SQLException {
        java.time.DayOfWeek day = java.time.LocalDate.now().getDayOfWeek();
        String dayName = "";
        switch (day) {
            case MONDAY: dayName = "Senin"; break;
            case TUESDAY: dayName = "Selasa"; break;
            case WEDNESDAY: dayName = "Rabu"; break;
            case THURSDAY: dayName = "Kamis"; break;
            case FRIDAY: dayName = "Jumat"; break;
            case SATURDAY: dayName = "Sabtu"; break;
            case SUNDAY: dayName = "Minggu"; break;
        }
        return scheduleDAO.findByDay(dayName);
    }
}