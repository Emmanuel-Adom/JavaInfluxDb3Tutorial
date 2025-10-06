package com.example.service;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Service class for InfluxDB operations
 * Handles all data writing and querying logic
 */
public class InfluxDBService {

    private final InfluxDBClient client;

    public InfluxDBService(InfluxDBClient client) {
        this.client = client;
    }

    /**
     * Writes sample sensor data to InfluxDB demonstrating different write methods
     */
    public void writeSampleData() throws Exception {
        System.out.println("\nWriting Sample Data");
        System.out.println("======================");

        writeUsingPointAPI();
        writeUsingLineProtocol();
        writeBatchData();

        Thread.sleep(1000); // Ensure data is available for querying
        System.out.println("Total data points written: 6");
    }

    /**
     * Write using Point API
     */
    private void writeUsingPointAPI() throws Exception {
        System.out.println("1. Writing data using Point API...");

        Point temperaturePoint = Point.measurement("temperature")
                .setTag("sensor_id", "TH01")
                .setTag("location", "warehouse")
                .setField("value", 23.2)
                .setTimestamp(Instant.now());

        Point humidityPoint = Point.measurement("humidity")
                .setTag("sensor_id", "HH01")
                .setTag("location", "warehouse")
                .setField("value", 65.1)
                .setTimestamp(Instant.now());

        client.writePoint(temperaturePoint);
        client.writePoint(humidityPoint);
        System.out.println("Point API data written: temperature=23.2°C, humidity=65.1%");
    }

    /**
     * Write using Line Protocol (high performance)
     */
    private void writeUsingLineProtocol() throws Exception {
        System.out.println("2. Writing data using Line Protocol...");

        String tempRecord = "temperature,sensor_id=TH02,location=office value=21.8";
        String humidityRecord = "humidity,sensor_id=HH02,location=office value=58.3";

        client.writeRecord(tempRecord);
        client.writeRecord(humidityRecord);
        System.out.println("Line Protocol data written: office sensors");
    }

    /**
     * Write batch data (multiple points at once)
     */
    private void writeBatchData() throws Exception {
        System.out.println("3. Writing batch data...");

        Point[] batchPoints = {
                Point.measurement("pressure")
                        .setTag("sensor_id", "PR01")
                        .setTag("location", "warehouse")
                        .setField("value", 1013.25)
                        .setTimestamp(Instant.now()),

                Point.measurement("pressure")
                        .setTag("sensor_id", "PR02")
                        .setTag("location", "office")
                        .setField("value", 1012.75)
                        .setTimestamp(Instant.now())
        };

        client.writePoints(List.of(batchPoints));
        System.out.println("Batch data written: pressure readings");
    }

    /**
     * Executes all query demonstrations
     */
    public void querySampleData() throws Exception {
        System.out.println("\nQuerying Sample Data");
        System.out.println("=======================");

        queryRecentTemperatures();
        queryWithParameters();
        queryAggregations();
        queryMultipleMeasurements();
        queryInfluxQL();
        printSummary();
    }

    /**
     * Basic SQL query for recent temperature readings
     */
    private void queryRecentTemperatures() throws Exception {
        System.out.println("1. SQL Query - Recent temperature readings:");

        String sqlQuery = "SELECT time, sensor_id, location, value FROM temperature ORDER BY time DESC LIMIT 5";

        try (Stream<Object[]> stream = client.query(sqlQuery)) {
            stream.forEach(row -> {
                String time = row[0] != null ? row[0].toString().substring(11, 19) : "null";
                System.out.printf("%s | %s | %s | %.1f°C%n",
                        time, row[1], row[2], row[3]);
            });
        }
        catch (Exception e) {
            System.out.println("SQL Query failed: " + e.getMessage());
        }

    }

    /**
     * Parametrized SQL query for secure querying
     */
    private void queryWithParameters() throws Exception {
        System.out.println("\n2. Parametrized SQL Query - Warehouse readings:");

        String paramQuery = "SELECT sensor_id, value FROM temperature WHERE location = $location";
        Map<String, Object> params = Map.of("location", "warehouse");

        try (Stream<Object[]> stream = client.query(paramQuery, params)) {
            stream.forEach(row ->
                    System.out.printf("%s: %.1f°C%n", row[0], row[1]));
        }
        catch (Exception e) {
            System.out.println("Parametrized SQL Query failed: " + e.getMessage());
        }
    }

    /**
     * Aggregation query for statistical analysis
     */
    private void queryAggregations() throws Exception {
        System.out.println("\n3. Aggregation Query - Average values by location:");

        String aggQuery = "SELECT location, AVG(value) as avg_temp, COUNT(*) as count " +
                "FROM temperature GROUP BY location ORDER BY avg_temp DESC";

        try (Stream<Object[]> stream = client.query(aggQuery)) {
            stream.forEach(row ->
                    System.out.printf("%s: avg=%.1f°C, count=%s%n",
                            row[0], row[1], row[2]));
        }
        catch (Exception e) {
            System.out.println("Aggregation Query failed: " + e.getMessage());
        }
    }

    /**
     * Multi-measurement query using UNION ALL
     */
    private void queryMultipleMeasurements() throws Exception {
        System.out.println("\n4. Multi-measurement Query - All sensor readings:");

        String multiQuery = "SELECT 'temperature' as type, sensor_id, location, value FROM temperature " +
                "UNION ALL " +
                "SELECT 'humidity' as type, sensor_id, location, value FROM humidity " +
                "ORDER BY sensor_id LIMIT 10";

        try (Stream<Object[]> stream = client.query(multiQuery)) {
            stream.forEach(row ->
                    System.out.printf("%s | %s | %s | %.1f%n",
                            row[0], row[1], row[2], row[3]));
        }
        catch (Exception e) {
            System.out.println("Multi-measurement Query failed: " + e.getMessage());
        }
    }

    /**
     * InfluxQL-specific query using aggregate functions
     * Uses InfluxQL syntax that is NOT valid in standard SQL
     */
    private void queryInfluxQL() {
        System.out.println("\n4. InfluxQL-Specific Queries:");

        // InfluxQL MEAN() function
        try {
            System.out.println("4a. InfluxQL MEAN() function:");

            String meanQuery = "SELECT MEAN(value) FROM temperature";

            try (Stream<Object[]> stream = client.query(meanQuery)) {
                stream.forEach(row ->
                        System.out.printf("Mean temperature: %.2f°C%n", row[0]));
            }
        } catch (Exception e) {
            System.out.println("InfluxQL MEAN() failed: " + e.getMessage());
        }
    }

    /**
     * Prints summary of demonstrated features
     */
    private void printSummary() {
        System.out.println("\nKey Features Demonstrated:");
        System.out.println("Point API writing");
        System.out.println("Line Protocol writing");
        System.out.println("Batch writing");
        System.out.println("SQL queries");
        System.out.println("Parametrized queries");
        System.out.println("Aggregation operations");
        System.out.println("Multi-measurement queries");
        System.out.println("InfluxQL queries");
    }
}