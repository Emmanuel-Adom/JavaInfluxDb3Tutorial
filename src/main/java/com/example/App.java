package com.example;

import com.example.config.ConfigurationManager;
import com.example.service.InfluxDBService;
import com.influxdb.v3.client.InfluxDBClient;

/**
 Main orchestrator
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Getting Started with Java and InfluxDB 3");
        System.out.println("===============================================");

        try {
            // Load configuration
            ConfigurationManager config = new ConfigurationManager();
            config.loadConfiguration();

            // Create InfluxDB client
            try (InfluxDBClient client = InfluxDBClient.getInstance(
                    config.getHost(),
                    config.getToken(),
                    config.getDatabase())) {

                System.out.println("Connected to InfluxDB 3 successfully!");
                System.out.println("Host: " + config.getHost());
                System.out.println("Database: " + config.getDatabase());

                // Create service and execute operations
                InfluxDBService service = new InfluxDBService(client);

                // Write sample data
                service.writeSampleData();

                // Query sample data
                service.querySampleData();

                System.out.println("Tutorial completed successfully!");
            }

        } catch (Exception e) {
            System.err.println("Tutorial failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}