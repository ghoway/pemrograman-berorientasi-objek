package com.undira.absenin.ui.panel;

import com.undira.absenin.model.Schedule;
import com.undira.absenin.service.ScheduleService;
import com.undira.absenin.ui.MainFrame;
import com.undira.absenin.ui.dialog.ScheduleFormDialog;
import java.awt.BorderLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SchedulePanel extends JPanel {

    private final ScheduleService scheduleService;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;

    public SchedulePanel() {
        this.scheduleService = new ScheduleService();
        initializeComponents();
        layoutComponents();
        loadScheduleData();
    }

    private void initializeComponents() {
        // Kolom ID disembunyikan dari user, tapi dibutuhkan untuk edit/delete
        String[] columnNames = {"ID", "Hari", "Jam Masuk", "Jam Pulang"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tidak ada edit langsung di tabel
            }
        };
        scheduleTable = new JTable(tableModel);
        // Sembunyikan kolom ID
        scheduleTable.getColumnModel().getColumn(0).setMinWidth(0);
        scheduleTable.getColumnModel().getColumn(0).setMaxWidth(0);
        scheduleTable.getColumnModel().getColumn(0).setWidth(0);
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
        
        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

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

        addButton.addActionListener(e -> openScheduleForm(null));
        editButton.addActionListener(e -> {
            int selectedRow = scheduleTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                Schedule schedule = findScheduleById(id);
                if (schedule != null) {
                    openScheduleForm(schedule);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih jadwal yang akan diedit.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedSchedule());
        refreshButton.addActionListener(e -> loadScheduleData());
    }
    
    private void openScheduleForm(Schedule schedule) {
        ScheduleFormDialog dialog = new ScheduleFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), schedule);
        dialog.setVisible(true);
        Schedule result = dialog.getSchedule();
        if (result != null) {
            try {
                if (result.getId() == 0) { // New schedule
                    scheduleService.addSchedule(result);
                } else { // Existing schedule
                    scheduleService.updateSchedule(result);
                }
                loadScheduleData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan jadwal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            String day = (String) tableModel.getValueAt(selectedRow, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus jadwal untuk hari " + day + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    scheduleService.deleteSchedule(id);
                    loadScheduleData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus jadwal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih jadwal yang akan dihapus.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadScheduleData() {
        tableModel.setRowCount(0);
        new Thread(() -> {
            try {
                List<Schedule> schedules = scheduleService.getAllSchedules();
                SwingUtilities.invokeLater(() -> {
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    for (Schedule s : schedules) {
                        Object[] rowData = {
                            s.getId(),
                            s.getDayOfWeek(),
                            s.getTimeIn().format(timeFormatter),
                            s.getTimeOut().format(timeFormatter)
                        };
                        tableModel.addRow(rowData);
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Gagal memuat data jadwal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    
    private Schedule findScheduleById(int id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((int) tableModel.getValueAt(i, 0) == id) {
                DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
                return new Schedule(
                    id,
                    (String) tableModel.getValueAt(i, 1),
                    LocalTime.parse((String) tableModel.getValueAt(i, 2), tf),
                    LocalTime.parse((String) tableModel.getValueAt(i, 3), tf)
                );
            }
        }
        return null;
    }
}