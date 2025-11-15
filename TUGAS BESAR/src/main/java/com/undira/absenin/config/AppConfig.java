package com.undira.absenin.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {

    private static final String CONFIG_FILE = "config.properties";
    public static DatabaseConfig databaseConfig = new DatabaseConfig();

    public static void load() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            databaseConfig.setHost(props.getProperty("db.host", "localhost"));
            databaseConfig.setPort(props.getProperty("db.port", "3306"));
            databaseConfig.setDatabaseName(props.getProperty("db.name", "tb_pbo"));
            databaseConfig.setUser(props.getProperty("db.user", "root"));
            databaseConfig.setPassword(props.getProperty("db.password", "root"));
        } catch (IOException e) {
            System.out.println("Config file not found. Using default values.");
        }
    }

    public static void save() {
        Properties props = new Properties();
        props.setProperty("db.host", databaseConfig.getHost());
        props.setProperty("db.port", databaseConfig.getPort());
        props.setProperty("db.name", databaseConfig.getDatabaseName());
        props.setProperty("db.user", databaseConfig.getUser());
        props.setProperty("db.password", databaseConfig.getPassword());

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "Absenin Application Database Configuration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}