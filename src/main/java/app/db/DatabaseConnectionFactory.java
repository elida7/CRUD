package app.db;

import app.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionFactory {

    private final DatabaseConfig config;

    public DatabaseConnectionFactory(DatabaseConfig config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        if (config.getUser() == null || config.getUser().isEmpty()) {
            return DriverManager.getConnection(config.getUrl());
        }
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }

    public DatabaseConfig getConfig() {
        return config;
    }
}

