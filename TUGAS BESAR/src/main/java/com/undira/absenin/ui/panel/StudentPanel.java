package com.undira.absenin.ui.panel;

import com.undira.absenin.model.Student;
import com.undira.absenin.service.StudentService;
import com.undira.absenin.ui.MainFrame;
import com.undira.absenin.ui.dialog.StudentFormDialog;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentPanel extends JPanel {

    private final StudentService studentService;
    private JTable studentTable;
    private DefaultTableModel tableModel;

    public StudentPanel() {
        this.studentService = new StudentService();
        initializeComponents();
        layoutComponents();
        loadStudentData();
    }

    private void initializeComponents() {
        String[] columnNames = {"ID", "NIM", "Nama", "Email", "No. HP"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
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
        
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> openStudentForm(null));
        editButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Student student = findStudentById(id);
                if (student != null) {
                    openStudentForm(student);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih data siswa yang akan diedit.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        refreshButton.addActionListener(e -> loadStudentData());
    }

    private void openStudentForm(Student student) {
        StudentFormDialog dialog = new StudentFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), student);
        dialog.setVisible(true);
        Student result = dialog.getStudent();
        if (result != null) {
            // Logika pemanggilan service sudah dipindah ke dalam dialog
            // Jadi di sini kita hanya perlu refresh data
            loadStudentData();
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String nama = (String) tableModel.getValueAt(selectedRow, 2);
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus siswa " + nama + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    studentService.deleteStudent(id);
                    loadStudentData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data siswa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data siswa yang akan dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadStudentData() {
        tableModel.setRowCount(0);
        new Thread(() -> {
            try {
                List<Student> students = studentService.getAllStudents();
                SwingUtilities.invokeLater(() -> {
                    for (Student s : students) {
                        Object[] rowData = {s.getId(), s.getNim(), s.getNama(), s.getEmail(), s.getNo_hp()};
                        tableModel.addRow(rowData);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Gagal memuat data siswa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    
    private Student findStudentById(int id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == id) {
                return new Student(
                    id,
                    (String) tableModel.getValueAt(i, 1),
                    (String) tableModel.getValueAt(i, 2),
                    (String) tableModel.getValueAt(i, 3),
                    (String) tableModel.getValueAt(i, 4)
                );
            }
        }
        return null;
    }
}