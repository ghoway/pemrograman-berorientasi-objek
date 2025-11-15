package com.undira.absenin.ui;

import com.undira.absenin.ui.panel.AttendancePanel;
import com.undira.absenin.ui.panel.DashboardPanel;
import com.undira.absenin.ui.panel.SchedulePanel;
import com.undira.absenin.ui.panel.StudentPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private DashboardPanel dashboardPanel;

    public MainFrame() {
        setTitle("Absenin - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        dashboardPanel = new DashboardPanel();
        StudentPanel studentPanel = new StudentPanel();
        AttendancePanel attendancePanel = new AttendancePanel();
        SchedulePanel schedulePanel = new SchedulePanel();

        cardPanel.add(dashboardPanel, "Dashboard");
        cardPanel.add(studentPanel, "Students");
        cardPanel.add(attendancePanel, "Attendance");
        cardPanel.add(schedulePanel, "Schedule");

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        JMenuItem studentItem = new JMenuItem("Manajemen Siswa");
        JMenuItem attendanceItem = new JMenuItem("Rekam Absensi");
        JMenuItem scheduleItem = new JMenuItem("Pengaturan Jadwal");
        JMenuItem exitItem = new JMenuItem("Keluar");

        menu.add(dashboardItem);
        menu.addSeparator();
        menu.add(studentItem);
        menu.add(attendanceItem);
        menu.add(scheduleItem);
        menu.addSeparator();
        menu.add(exitItem);
        menuBar.add(menu);

        setJMenuBar(menuBar);

        dashboardItem.addActionListener(e -> showPanel("Dashboard"));
        studentItem.addActionListener(e -> showPanel("Students"));
        attendanceItem.addActionListener(e -> showPanel("Attendance"));
        scheduleItem.addActionListener(e -> showPanel("Schedule"));
        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        add(cardPanel, BorderLayout.CENTER);
    }

    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        if (panelName.equals("Dashboard")) {
            dashboardPanel.refreshStats();
        }
    }
    
    // Metode baru untuk dipanggil dari panel lain
    public void showDashboard() {
        showPanel("Dashboard");
    }
}