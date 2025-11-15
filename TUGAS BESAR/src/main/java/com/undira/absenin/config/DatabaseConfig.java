package com.undira.absenin.config;

public class DatabaseConfig {
    private String host;
    private String port;
    private String databaseName;
    private String user;
    private String password;

    public DatabaseConfig() {}

    public DatabaseConfig(String host, String port, String databaseName, String user, String password) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.user = user;
        this.password = password;
    }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public String getPort() { return port; }
    public void setPort(String port) { this.port = port; }
    public String getDatabaseName() { return databaseName; }
    public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
    }
}