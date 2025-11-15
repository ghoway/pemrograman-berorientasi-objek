package com.undira.absenin.ui.panel;

import com.undira.absenin.service.AttendanceService;
import com.undira.absenin.service.StudentService;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class DashboardPanel extends JPanel {

    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private JLabel timeLabel;
    private JLabel totalStudentsLabel;
    private JLabel attendedTodayLabel;

    public DashboardPanel() {
        this.studentService = new StudentService();
        this.attendanceService = new AttendanceService();
        initializeComponents();
        layoutComponents();
        startClock();
        refreshStats();
    }

    private void initializeComponents() {
        timeLabel = new JLabel("", JLabel.CENTER);
        timeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        totalStudentsLabel = new JLabel("Total Siswa: -", JLabel.CENTER);
        totalStudentsLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        attendedTodayLabel = new JLabel("Sudah Absen Hari Ini: -", JLabel.CENTER);
        attendedTodayLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        statsPanel.add(totalStudentsLabel);
        statsPanel.add(attendedTodayLabel);

        add(timeLabel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
    }

    private void startClock() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss");
        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(LocalDateTime.now().format(dtf));
        });
        timer.start();
    }

    public void refreshStats() {
        new Thread(() -> {
            try {
                int totalStudents = studentService.getAllStudents().size();
                int attendedToday = attendanceService.getAttendanceCountForToday();

                java.awt.EventQueue.invokeLater(() -> {
                    totalStudentsLabel.setText("Total Siswa: " + totalStudents);
                    attendedTodayLabel.setText("Sudah Absen Hari Ini: " + attendedToday);
                });
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}