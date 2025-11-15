package com.undira.absenin.service;

public class AuthService {
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin";

    public static boolean login(String username, String password) {
        return DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password);
    }
}