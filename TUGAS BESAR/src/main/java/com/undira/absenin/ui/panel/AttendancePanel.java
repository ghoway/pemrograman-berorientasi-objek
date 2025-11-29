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
    
    // === PERUBAHAN PERTAMA: Metode handleScanAction yang baru ===
    private void handleScanAction() {
        // Dapatkan frame induk untuk dijadikan parent dialog
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        // Buat scanner baru dan berikan referensi 'this' (AttendancePanel) kepadanya
        QRCodeScannerDialog scanner = new QRCodeScannerDialog(parentFrame, this);
        
        // Tampilkan scanner. Logika pemrosesan ada di dalam dialog sekarang.
        scanner.setVisible(true);
    }

    // === PERUBAHAN KEDUA: Metode publik untuk refresh ===
    /**
     * Metode publik untuk me-refresh data tabel absensi.
     * Dipanggil oleh QRCodeScannerDialog setelah berhasil absen.
     */
    public void refreshAttendanceTable() {
        // Cukup panggil metode load yang sudah ada
        loadAttendanceData();
    }

    private void loadAttendanceData() {
    System.out.println("Memulai proses load data absensi..."); // PENANDA 1
    tableModel.setRowCount(0); // Kosongkan tabel dulu
    
    new Thread(() -> {
        try {
            System.out.println("Menghubungi service untuk mengambil data..."); // PENANDA 2
            List<Attendance> attendances = attendanceService.getAttendancesForToday();
            
            // PENANDA 3: Cek jumlah data yang didapat
            System.out.println("Jumlah data yang didapat dari database: " + attendances.size());
            
            // Cetak juga detail datanya untuk memastikan
            for (Attendance a : attendances) {
                System.out.println(" -> Data: " + a.getStudentName() + ", Status: " + a.getStatus());
            }

            SwingUtilities.invokeLater(() -> {
                System.out.println("Memulai proses menambahkan data ke tabel..."); // PENANDA 4
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
                System.out.println("Proses load data selesai."); // PENANDA 5
            });
        } catch (Exception ex) {
            // PENANDA 6: Cetak error jika ada
            System.err.println("TERJADI ERROR SAAT LOAD DATA: " + ex.getMessage());
            ex.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Gagal memuat data absensi: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
        }
    }).start();
}
}