package com.example.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConfigurationManager Tests")
class ConfigurationManagerTest {

    private ConfigurationManager configManager;

    @BeforeEach
    void setUp() {
        configManager = new ConfigurationManager();
    }

    @AfterEach
    void tearDown() {
        if (configManager != null) {
            configManager.resetConfiguration();
        }
    }

    @Test
    @DisplayName("Given valid application.properties exists, When loading configuration, Then should load successfully")
    void givenValidPropertiesFile_WhenLoadingConfiguration_ThenShouldLoadSuccessfully() throws IOException {
        // When
        configManager.loadConfiguration();

        // Then
        assertNotNull(configManager.getHost(), "Host should not be null");
        assertNotNull(configManager.getDatabase(), "Database should not be null");
        assertNotNull(configManager.getToken(), "Token should not be null");
        assertFalse(configManager.getHost().trim().isEmpty(), "Host should not be empty");
        assertFalse(configManager.getDatabase().trim().isEmpty(), "Database should not be empty");
        assertTrue(configManager.getToken().length > 0, "Token should not be empty");
    }

    @Test
    @DisplayName("Given configuration is loaded, When getting host, Then should return non-empty string")
    void givenConfigurationLoaded_WhenGettingHost_ThenShouldReturnNonEmptyString() throws IOException {
        // Given
        configManager.loadConfiguration();

        // When
        String host = configManager.getHost();

        // Then
        assertNotNull(host);
        assertFalse(host.trim().isEmpty());
        assertTrue(host.startsWith("http"), "Host should start with http or https");
    }

    @Test
    @DisplayName("Given configuration is loaded, When getting database, Then should return non-empty string")
    void givenConfigurationLoaded_WhenGettingDatabase_ThenShouldReturnNonEmptyString() throws IOException {
        // Given
        configManager.loadConfiguration();

        // When
        String database = configManager.getDatabase();

        // Then
        assertNotNull(database);
        assertFalse(database.trim().isEmpty());
    }

    @Test
    @DisplayName("Given configuration is loaded, When getting token, Then should return char array")
    void givenConfigurationLoaded_WhenGettingToken_ThenShouldReturnCharArray() throws IOException {
        // Given
        configManager.loadConfiguration();

        // When
        char[] token = configManager.getToken();

        // Then
        assertNotNull(token);
        assertTrue(token.length > 0);
    }

    @Test
    @DisplayName("Given configuration is loaded, When getting token twice, Then should return different array instances")
    void givenConfigurationLoaded_WhenGettingTokenTwice_ThenShouldReturnDifferentInstances() throws IOException {
        // Given
        configManager.loadConfiguration();

        // When
        char[] token1 = configManager.getToken();
        char[] token2 = configManager.getToken();

        // Then
        assertNotSame(token1, token2, "Token arrays should be different instances (cloned)");
        assertArrayEquals(token1, token2, "Token array contents should be equal");
    }

    @Test
    @DisplayName("Given configuration is loaded, When resetting configuration, Then all values should be null")
    void givenConfigurationLoaded_WhenResettingConfiguration_ThenAllValuesShouldBeNull() throws IOException {
        // Given
        configManager.loadConfiguration();
        assertNotNull(configManager.getHost());

        // When
        configManager.resetConfiguration();

        // Then
        assertNull(configManager.getHost());
        assertNull(configManager.getDatabase());
        assertNull(configManager.getToken());
    }

    @Test
    @DisplayName("Given ConfigurationManager instance, When creating new instance, Then should be independent")
    void givenConfigurationManagerInstance_WhenCreatingNewInstance_ThenShouldBeIndependent() throws IOException {
        // Given
        ConfigurationManager config1 = new ConfigurationManager();
        config1.loadConfiguration();

        // When
        ConfigurationManager config2 = new ConfigurationManager();

        // Then
        assertNull(config2.getHost(), "New instance should have null host");
        assertNotNull(config1.getHost(), "Original instance should still have host");
    }
}