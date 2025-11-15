package com.undira.absenin.ui.panel;

import com.undira.absenin.model.Attendance;
import com.undira.absenin.service.AttendanceService;
import com.undira.absenin.ui.MainFrame;
import com.undira.absenin.ui.dialog.QRCodeScannerDialog;
import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AttendancePanel extends JPanel {

    private final AttendanceService attendanceService;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    public AttendancePanel() {
        this.attendanceService = new AttendanceService();
        initializeComponents();
        layoutComponents();
        loadAttendanceData();
    }

    private void initializeComponents() {
        String[] columnNames = {"ID", "NIM Siswa", "Nama Siswa", "Waktu Absen", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(tableModel);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        
        // Panel untuk tombol Back
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        JButton backButton = new JButton("<< Back to Dashboard");
        backButton.addActionListener(e -> {
            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            mainFrame.showDashboard();
        });
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        
        add(new JScrollPane(attendanceTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton scanButton = new JButton("Scan QR Code untuk Absen");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(scanButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        scanButton.addActionListener(e -> handleScanAction());
        refreshButton.addActionListener(e -> loadAttendanceData());
    }
    
    private void handleScanAction() {
        QRCodeScannerDialog scanner = new QRCodeScannerDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        scanner.setVisible(true);
        String nim = scanner.getScannedResult();
        
        if (nim != null) {
            new Thread(() -> {
                try {
                    String message = attendanceService.processAttendance(nim);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, message, "Hasil Absensi", JOptionPane.INFORMATION_MESSAGE);
                        loadAttendanceData();
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                }
            }).start();
        }
    }

    private void loadAttendanceData() {
        tableModel.setRowCount(0);
        new Thread(() -> {
            try {
                List<Attendance> attendances = attendanceService.getAttendancesForToday();
                SwingUtilities.invokeLater(() -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    for (Attendance a : attendances) {
                        Object[] rowData = {
                            a.getId(),
                            "ID: " + a.getStudentId(),
                            a.getStudentName(),
                            a.getAttendanceTime().format(formatter),
                            a.getStatus()
                        };
                        tableModel.addRow(rowData);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Gagal memuat data absensi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
}