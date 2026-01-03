package com.undira.tbwahyu.ui;

import com.undira.tbwahyu.auth.AuthService;
import com.undira.tbwahyu.auth.User;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private User loggedInUser = null;

    public LoginDialog() {
        super((java.awt.Frame) null, "Login Sistem Manajemen Proyek", true);
        initComponents();
        setResizable(false);
        setLocationRelativeTo(null);
        pack();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        add(new javax.swing.JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        add(new javax.swing.JLabel("Password:"), gbc);
        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        add(txtPassword, gbc);

        // Buttons
        gbc.gridwidth = 2;
        gbc.gridy = 2;
        javax.swing.JPanel panelBtn = new javax.swing.JPanel();
        javax.swing.JButton btnLogin = new javax.swing.JButton("Login");
        javax.swing.JButton btnCancel = new javax.swing.JButton("Cancel");

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });

        btnCancel.addActionListener(e -> dispose());

        panelBtn.add(btnLogin);
        panelBtn.add(btnCancel);
        add(panelBtn, gbc);

        // Enter key login
        getRootPane().setDefaultButton(btnLogin);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password wajib diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            AuthService auth = new AuthService();
            loggedInUser = auth.login(username, password);

            if (loggedInUser != null) {
                dispose(); // tutup dialog, lanjut ke main form
            } else {
                JOptionPane.showMessageDialog(this, "Username / password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtUsername.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error koneksi DB: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }
}
