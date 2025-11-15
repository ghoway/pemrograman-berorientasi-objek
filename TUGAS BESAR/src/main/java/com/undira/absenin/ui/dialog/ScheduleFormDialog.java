package com.undira.absenin.ui.dialog;

import com.undira.absenin.model.Schedule;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;

public class ScheduleFormDialog extends JDialog {

    private JComboBox<String> dayComboBox;
    private JTextField timeInField;
    private JTextField timeOutField;
    private boolean saved = false;
    private Schedule schedule;

    public ScheduleFormDialog(JFrame parent, Schedule schedule) {
        super(parent, schedule == null ? "Tambah Jadwal" : "Edit Jadwal", true);
        this.schedule = schedule;
        initializeComponents();
        layoutComponents();
        if (schedule != null) {
            loadScheduleData();
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        String[] days = {"Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"};
        dayComboBox = new JComboBox<>(days);
        timeInField = new JTextField("07:30");
        timeOutField = new JTextField("13:30");
    }

    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.add(new JLabel("Hari:"));
        formPanel.add(dayComboBox);
        formPanel.add(new JLabel("Jam Masuk (HH:MM):"));
        formPanel.add(timeInField);
        formPanel.add(new JLabel("Jam Pulang (HH:MM):"));
        formPanel.add(timeOutField);

        JButton saveButton = new JButton("Simpan");
        JButton cancelButton = new JButton("Batal");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> saveSchedule());
        cancelButton.addActionListener(e -> dispose());

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadScheduleData() {
        dayComboBox.setSelectedItem(schedule.getDayOfWeek());
        timeInField.setText(schedule.getTimeIn().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeOutField.setText(schedule.getTimeOut().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void saveSchedule() {
        try {
            LocalTime timeIn = LocalTime.parse(timeInField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime timeOut = LocalTime.parse(timeOutField.getText(), DateTimeFormatter.ofPattern("HH:mm"));

            if (timeOut.isBefore(timeIn) || timeOut.equals(timeIn)) {
                JOptionPane.showMessageDialog(this, "Jam Pulang harus setelah Jam Masuk.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (schedule == null) {
                schedule = new Schedule();
            }
            schedule.setDayOfWeek((String) dayComboBox.getSelectedItem());
            schedule.setTimeIn(timeIn);
            schedule.setTimeOut(timeOut);

            saved = true;
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format waktu tidak valid. Gunakan format HH:MM (contoh: 08:00).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Schedule getSchedule() {
        return saved ? schedule : null;
    }
}