package com.undira.absenin.ui.dialog;

// PASTIKAN IMPORT INI ADA
import javax.swing.*;
import java.awt.*;

public class AttendanceResultDialog extends JDialog {

    public AttendanceResultDialog(Frame parent, String nim, String nama, String waktu, String status) {
        super(parent, "Hasil Absensi", true); // true = modal, harus ditutup dulu

        // Panel utama
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("NIM:"));
        panel.add(new JLabel(nim));
        panel.add(new JLabel("Nama:"));
        panel.add(new JLabel(nama));
        panel.add(new JLabel("Waktu Absen:"));
        panel.add(new JLabel(waktu));
        panel.add(new JLabel("Status:"));
        
        // Buat label status lebih menonjol
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 14));
        if ("TERLAMBAT".equals(status)) {
            statusLabel.setForeground(Color.ORANGE);
        } else if ("TEPAT WAKTU".equals(status)) {
            statusLabel.setForeground(Color.GREEN);
        } else {
            statusLabel.setForeground(Color.BLACK);
        }
        panel.add(statusLabel);

        // Tombol OK
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        this.add(panel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
}