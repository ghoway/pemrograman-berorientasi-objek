package com.undira.absenin.ui.dialog;

import com.undira.absenin.model.Student;
import com.undira.absenin.service.StudentService;
import com.undira.absenin.util.QRCodeGenerator;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class StudentFormDialog extends JDialog {

    private JTextField nimField;
    private JTextField namaField;
    private JTextField emailField;
    private JTextField noHpField;
    private boolean saved = false;
    private Student student;
    private final StudentService studentService;

    public StudentFormDialog(JFrame parent, Student student) {
        super(parent, student == null ? "Tambah Siswa" : "Edit Siswa", true);
        this.student = student;
        this.studentService = new StudentService();
        initializeComponents();
        layoutComponents();
        if (student != null) {
            loadStudentData();
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        nimField = new JTextField();
        namaField = new JTextField();
        emailField = new JTextField();
        noHpField = new JTextField();
    }

    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.add(new JLabel("NIM:"));
        formPanel.add(nimField);
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(namaField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("No. HP:"));
        formPanel.add(noHpField);

        JButton saveButton = new JButton("Simpan");
        JButton cancelButton = new JButton("Batal");
        JButton generateQRButton = new JButton("Generate QR Code");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        if (student != null) {
            buttonPanel.add(generateQRButton);
        }

        saveButton.addActionListener(e -> saveStudent());
        cancelButton.addActionListener(e -> dispose());
        generateQRButton.addActionListener(e -> showQRCode());

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadStudentData() {
        nimField.setText(student.getNim());
        namaField.setText(student.getNama());
        emailField.setText(student.getEmail());
        noHpField.setText(student.getNo_hp());
        nimField.setEditable(false);
    }

    private void saveStudent() {
        if (nimField.getText().trim().isEmpty() || namaField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIM dan Nama harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (student == null) {
            student = new Student();
        }
        student.setNim(nimField.getText().trim());
        student.setNama(namaField.getText().trim());
        student.setEmail(emailField.getText().trim());
        student.setNo_hp(noHpField.getText().trim());

        try {
            if (student.getId() == 0) { // New student
                Student addedStudent = studentService.addStudent(student);
                // Generate dan simpan QR Code setelah siswa berhasil ditambahkan
                String qrPath = QRCodeGenerator.generateAndSaveQRCode(addedStudent.getNim(), addedStudent.getNama());
                JOptionPane.showMessageDialog(this, "Siswa berhasil ditambahkan!\nQR Code disimpan di:\n" + qrPath, "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else { // Existing student
                studentService.updateStudent(student);
                JOptionPane.showMessageDialog(this, "Data siswa berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data siswa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showQRCode() {
        try {
            BufferedImage qrImage = QRCodeGenerator.generateQRCodeImage(student.getNim(), 200, 200);
            ImageIcon icon = new ImageIcon(qrImage);
            JLabel label = new JLabel(icon);
            JOptionPane.showMessageDialog(this, label, "QR Code untuk " + student.getNama(), JOptionPane.PLAIN_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal generate QR Code: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Student getStudent() {
        return saved ? student : null;
    }
}