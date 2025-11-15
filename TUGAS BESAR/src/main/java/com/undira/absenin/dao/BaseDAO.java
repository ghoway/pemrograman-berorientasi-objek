package com.undira.absenin.dao;

import com.undira.absenin.config.AppConfig;
import com.undira.absenin.config.DatabaseConfig;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class BaseDAO {

    public static void setupDatabaseAndMigrate() throws Exception {
        DatabaseConfig config = AppConfig.databaseConfig;
        
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://" + config.getHost() + ":" + config.getPort(),
                config.getUser(), config.getPassword())) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + config.getDatabaseName());
            }
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            InputStream inputStream = BaseDAO.class.getClassLoader().getResourceAsStream("migration.sql");
            if (inputStream == null) {
                throw new Exception("Migration file not found in resources.");
            }
            
            String script = new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));
            
            String[] commands = script.split(";");
            for (String command : commands) {
                if (!command.trim().isEmpty()) {
                    stmt.addBatch(command);
                }
            }
            stmt.executeBatch();
        }
    }
}