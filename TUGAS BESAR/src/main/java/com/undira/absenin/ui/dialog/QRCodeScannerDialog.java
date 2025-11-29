package com.undira.absenin.ui.dialog;

import com.undira.absenin.model.Attendance;
import com.undira.absenin.service.AttendanceService;
import com.undira.absenin.ui.panel.AttendancePanel;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.swing.*;
import java.awt.*;
import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class QRCodeScannerDialog extends JDialog {

    private String scannedResult = null;
    private Webcam webcam = null;
    private WebcamPanel webcamPanel = null;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final AttendancePanel attendancePanel;
    private final AttendanceService attendanceService;

    public QRCodeScannerDialog(JFrame parent, AttendancePanel attendancePanel) {
        super(parent, "Scan QR Code", false);
        this.attendancePanel = attendancePanel;
        this.attendanceService = new AttendanceService();
        
        initializeComponents();
        startScanning();
        pack();
        setLocationRelativeTo(parent);
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                stopWebcam();
            }
        });
    }

    private void initializeComponents() {
        setTitle("Scanning QR Code...");
        setLayout(new BorderLayout());
        
        JLabel instructionLabel = new JLabel("Arahkan QR Code ke kamera...", JLabel.CENTER);
        add(instructionLabel, BorderLayout.NORTH);
    }

    private void startScanning() {
    executor.execute(() -> {
        webcam = Webcam.getDefault();
        if (webcam == null) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Tidak ada kamera yang terdeteksi!", "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            });
            return;
        }

        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();

        SwingUtilities.invokeLater(() -> {
            webcamPanel = new WebcamPanel(webcam);
            webcamPanel.setImageSizeDisplayed(true);
            webcamPanel.setFPSDisplayed(false);
            add(webcamPanel, BorderLayout.CENTER);
            revalidate();
            repaint();
        });

        while (webcam.isOpen()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }

            BufferedImage image = webcam.getImage();
            if (image != null) {
                Result result = scanImage(image);
                if (result != null) {
                    this.scannedResult = result.getText();
                    System.out.println("QR Code terdeteksi: " + this.scannedResult);

                    try {
                        String nim = this.scannedResult;
                        // --- PERBAIKAN: Tangkap pesan dari processAttendance ---
                        String processMessage = attendanceService.processAttendance(nim);
                        System.out.println("Pesan dari service: " + processMessage);

                        // --- PERBAIKAN: Cek isi pesan ---
                        if (processMessage.startsWith("Error")) {
                            // Jika error, tampilkan pesan error dan JANGAN refresh
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(this, processMessage, "Error", JOptionPane.ERROR_MESSAGE);
                            });
                        } else {
                            // Jika sukses atau warning, cari data terbaru untuk ditampilkan
                            List<Attendance> attendances = attendanceService.getAttendancesForToday();
                            Attendance latestAttendance = null;
                            if (!attendances.isEmpty()) {
                                for(Attendance a : attendances) {
                                    if (String.valueOf(a.getStudentId()).equals(nim)) {
                                        latestAttendance = a;
                                        break;
                                    }
                                }
                            }
                            
                            if (latestAttendance != null) {
                                // Buat variabel final untuk lambda
                                final Attendance attendanceToShow = latestAttendance;
                                String nama = attendanceToShow.getStudentName();
                                String waktu = attendanceToShow.getAttendanceTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                                String status = attendanceToShow.getStatus();

                                SwingUtilities.invokeLater(() -> {
                                    AttendanceResultDialog resultDialog = new AttendanceResultDialog(
                                        (Frame) SwingUtilities.getWindowAncestor(this),
                                        "ID: " + attendanceToShow.getStudentId(), 
                                        nama, 
                                        waktu, 
                                        status
                                    );
                                    resultDialog.setVisible(true);
                                });
                            }
                            
                            // --- REFRESH PANEL PENJENDELA INDUK ---
                            // Ini hanya dijalankan jika proses berhasil
                            attendancePanel.refreshAttendanceTable();
                        }

                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
                    }
                    
                    // Jeda untuk mencegah scan ulang
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }
    });
}
    private void stopWebcam() {
        if (webcam != null && webcam.isOpen()) {
            webcam.close();
        }
    }

    private Result scanImage(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            return new MultiFormatReader().decode(bitmap);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public String getScannedResult() {
        return scannedResult;
    }
}