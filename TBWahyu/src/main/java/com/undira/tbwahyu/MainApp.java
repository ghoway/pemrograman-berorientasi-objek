package com.undira.tbwahyu;

import com.undira.tbwahyu.auth.User;
import com.undira.tbwahyu.ui.FormProyek;
import com.undira.tbwahyu.ui.LoginDialog;

import javax.swing.SwingUtilities;

public class MainApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::showLogin);
    }

    public static void showLogin() {
        LoginDialog login = new LoginDialog();
        login.setVisible(true);

        User user = login.getLoggedInUser();
        if (user != null) {
            showFormProyek(user);
        }
        // ❌ TIDAK ADA System.exit(0)
    }

    public static void showFormProyek(User user) {
        FormProyek form = new FormProyek(user);
        form.setVisible(true);
    }
}


/**
 * 1. Admin menginput
 */