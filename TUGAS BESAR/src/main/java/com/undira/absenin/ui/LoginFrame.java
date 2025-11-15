package com.undira.absenin.ui;

import com.undira.absenin.service.AuthService;
import com.undira.absenin.ui.dialog.DatabaseSetupDialog;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Absenin - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null);
        initializeComponents();
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField("admin");
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField("admin");

        JButton loginButton = new JButton("Login");
        JButton setupDbButton = new JButton("Setup Database");

        mainPanel.add(userLabel);
        mainPanel.add(userField);
        mainPanel.add(passLabel);
        mainPanel.add(passField);
        mainPanel.add(new JLabel());
        mainPanel.add(loginButton);
        mainPanel.add(new JLabel());
        mainPanel.add(setupDbButton);

        add(mainPanel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (AuthService.login(username, password)) {
                dispose();
                new MainFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        });

        setupDbButton.addActionListener(e -> new DatabaseSetupDialog(this).setVisible(true));
        
        getRootPane().setDefaultButton(loginButton);
    }
}