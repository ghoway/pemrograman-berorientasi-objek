package com.undira.absenin.ui.dialog;

import com.undira.absenin.config.AppConfig;
import com.undira.absenin.config.DatabaseConfig;
import com.undira.absenin.dao.BaseDAO;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.DriverManager;
import javax.swing.*;

public class DatabaseSetupDialog extends JDialog {

    private JTextField hostField;
    private JTextField portField;
    private JTextField dbNameField;
    private JTextField userField;
    private JPasswordField passwordField;

    public DatabaseSetupDialog(JFrame parent) {
        super(parent, "Setup Database", true);
        initializeComponents();
        layoutComponents();
        loadCurrentConfig();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        hostField = new JTextField(AppConfig.databaseConfig.getHost());
        portField = new JTextField(AppConfig.databaseConfig.getPort());
        dbNameField = new JTextField(AppConfig.databaseConfig.getDatabaseName());
        userField = new JTextField(AppConfig.databaseConfig.getUser());
        passwordField = new JPasswordField(AppConfig.databaseConfig.getPassword());
    }

    private void layoutComponents() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Host:"));
        formPanel.add(hostField);
        formPanel.add(new JLabel("Port:"));
        formPanel.add(portField);
        formPanel.add(new JLabel("Database Name:"));
        formPanel.add(dbNameField);
        formPanel.add(new JLabel("User:"));
        formPanel.add(userField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel();
        JButton testButton = new JButton("Test Connection");
        JButton saveButton = new JButton("Save & Migrate");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(testButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(saveButton);

        testButton.addActionListener(e -> testConnection());
        saveButton.addActionListener(e -> saveAndMigrate());
        cancelButton.addActionListener(e -> dispose());
    }

    private void loadCurrentConfig() {
        // Fields are already initialized with current config
    }

    private void testConnection() {
        DatabaseConfig tempConfig = new DatabaseConfig(
                hostField.getText(), portField.getText(), dbNameField.getText(),
                userField.getText(), new String(passwordField.getPassword())
        );
        try (var conn = DriverManager.getConnection(tempConfig.getUrl(), tempConfig.getUser(), tempConfig.getPassword())) {
            JOptionPane.showMessageDialog(this, "Koneksi Berhasil!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Koneksi Gagal: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAndMigrate() {
        AppConfig.databaseConfig.setHost(hostField.getText());
        AppConfig.databaseConfig.setPort(portField.getText());
        AppConfig.databaseConfig.setDatabaseName(dbNameField.getText());
        AppConfig.databaseConfig.setUser(userField.getText());
        AppConfig.databaseConfig.setPassword(new String(passwordField.getPassword()));

        try {
            BaseDAO.setupDatabaseAndMigrate();
            AppConfig.save();
            JOptionPane.showMessageDialog(this, "Database berhasil disetup dan dimigrasi!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal setup/migrasi database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}