package com.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages application configuration from properties file
 */
public class ConfigurationManager {

    private String host;
    private String database;
    private char[] token;

    /**
     * Loads configuration from application.properties file
     */
    public void loadConfiguration() throws IOException {
        Properties props = new Properties();

        try (InputStream input = ConfigurationManager.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new IOException("Unable to find application.properties file");
            }

            props.load(input);

            // Extract configuration values
            host = props.getProperty("INFLUXDB_HOST");
            database = props.getProperty("INFLUXDB_DATABASE");
            String tokenString = props.getProperty("INFLUXDB_TOKEN");

            // Validate required properties
            validateProperty(host, "INFLUXDB_HOST");
            validateProperty(database, "INFLUXDB_DATABASE");
            validateProperty(tokenString, "INFLUXDB_TOKEN");

            // Convert token to char array for security
            token = tokenString.toCharArray();

            System.out.println("Configuration loaded successfully");

        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Validates that a property value is not null or empty
     */
    private void validateProperty(String value, String propertyName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(propertyName + " is required in application.properties");
        }
    }

    // Getters
    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public char[] getToken() {
        return token != null ? token.clone() : null;
    }

    /**
     * Resets configuration
     */
    public void resetConfiguration() {
        host = null;
        database = null;
        token = null;
    }
}