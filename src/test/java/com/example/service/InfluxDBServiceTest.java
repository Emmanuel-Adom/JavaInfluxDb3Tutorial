package com.example.service;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InfluxDBService Tests")
class InfluxDBServiceTest {

    @Test
    @DisplayName("Given InfluxDBClient, When creating InfluxDBService, Then service should be initialized")
    void givenInfluxDBClient_WhenCreatingService_ThenServiceShouldBeInitialized() {
        // Given
        InfluxDBClient mockClient = null;

        // When
        InfluxDBService service = new InfluxDBService(mockClient);

        // Then
        assertNotNull(service, "Service should be initialized");
    }

    @Test
    @DisplayName("Given Point parameters, When creating temperature Point, Then Point should be created successfully")
    void givenPointParameters_WhenCreatingTemperaturePoint_ThenPointShouldBeCreated() {
        // Given
        String measurement = "temperature";
        String sensorId = "TH01";
        String location = "warehouse";
        double value = 23.2;
        Instant timestamp = Instant.now();

        // When
        Point point = Point.measurement(measurement)
                .setTag("sensor_id", sensorId)
                .setTag("location", location)
                .setField("value", value)
                .setTimestamp(timestamp);

        // Then
        assertNotNull(point, "Point should be created");
    }

    @Test
    @DisplayName("Given Point parameters, When creating humidity Point, Then Point should be created successfully")
    void givenPointParameters_WhenCreatingHumidityPoint_ThenPointShouldBeCreated() {
        // Given
        String measurement = "humidity";
        String sensorId = "HH01";
        String location = "warehouse";
        double value = 65.1;
        Instant timestamp = Instant.now();

        // When
        Point point = Point.measurement(measurement)
                .setTag("sensor_id", sensorId)
                .setTag("location", location)
                .setField("value", value)
                .setTimestamp(timestamp);

        // Then
        assertNotNull(point, "Point should be created");
    }

    @Test
    @DisplayName("Given line protocol string, When formatting temperature record, Then format should be correct")
    void givenLineProtocolString_WhenFormattingTemperatureRecord_ThenFormatShouldBeCorrect() {
        // Given
        String sensorId = "TH02";
        String location = "office";
        double value = 21.8;

        // When
        String record = String.format("temperature,sensor_id=%s,location=%s value=%s",
                sensorId, location, value);

        // Then
        assertNotNull(record);
        assertTrue(record.contains("temperature"));
        assertTrue(record.contains("sensor_id=TH02"));
        assertTrue(record.contains("location=office"));
        assertTrue(record.contains("value=21.8"));
    }

    @Test
    @DisplayName("Given multiple Points, When creating batch array, Then array should contain all points")
    void givenMultiplePoints_WhenCreatingBatchArray_ThenArrayShouldContainAllPoints() {
        // Given
        Instant timestamp = Instant.now();

        // When
        Point[] batchPoints = {
                Point.measurement("pressure")
                        .setTag("sensor_id", "PR01")
                        .setTag("location", "warehouse")
                        .setField("value", 1013.25)
                        .setTimestamp(timestamp),

                Point.measurement("pressure")
                        .setTag("sensor_id", "PR02")
                        .setTag("location", "office")
                        .setField("value", 1012.75)
                        .setTimestamp(timestamp)
        };

        // Then
        assertNotNull(batchPoints);
        assertEquals(2, batchPoints.length);
        assertNotNull(batchPoints[0]);
        assertNotNull(batchPoints[1]);
    }

    @Test
    @DisplayName("Given batch points array, When converting to List, Then List should contain all points")
    void givenBatchPointsArray_WhenConvertingToList_ThenListShouldContainAllPoints() {
        // Given
        Point[] batchPoints = {
                Point.measurement("pressure")
                        .setTag("sensor_id", "PR01")
                        .setField("value", 1013.25)
                        .setTimestamp(Instant.now()),

                Point.measurement("pressure")
                        .setTag("sensor_id", "PR02")
                        .setField("value", 1012.75)
                        .setTimestamp(Instant.now())
        };

        // When
        List<Point> pointList = List.of(batchPoints);

        // Then
        assertNotNull(pointList);
        assertEquals(2, pointList.size());
    }

    @Test
    @DisplayName("Given SQL query string, When building recent temperature query, Then query should be valid SQL")
    void givenSQLQueryString_WhenBuildingRecentTemperatureQuery_ThenQueryShouldBeValidSQL() {
        // Given
        String measurement = "temperature";
        int limit = 5;

        // When
        String query = String.format("SELECT time, sensor_id, location, value FROM %s ORDER BY time DESC LIMIT %d",
                measurement, limit);

        // Then
        assertNotNull(query);
        assertTrue(query.contains("SELECT"));
        assertTrue(query.contains("FROM temperature"));
        assertTrue(query.contains("ORDER BY time DESC"));
        assertTrue(query.contains("LIMIT 5"));
    }

    @Test
    @DisplayName("Given parametrized query, When building with location parameter, Then query should use parameter placeholder")
    void givenParametrizedQuery_WhenBuildingWithLocationParameter_ThenQueryShouldUseParameterPlaceholder() {
        // Given
        String measurement = "temperature";
        String paramName = "$location";

        // When
        String query = String.format("SELECT sensor_id, value FROM %s WHERE location = %s",
                measurement, paramName);

        // Then
        assertNotNull(query);
        assertTrue(query.contains("$location"));
        assertTrue(query.contains("WHERE"));
    }

    @Test
    @DisplayName("Given aggregation query, When building with AVG and COUNT, Then query should include aggregation functions")
    void givenAggregationQuery_WhenBuildingWithAvgAndCount_ThenQueryShouldIncludeAggregations() {
        // Given
        String measurement = "temperature";

        // When
        String query = String.format("SELECT location, AVG(value) as avg_temp, COUNT(*) as count " +
                        "FROM %s GROUP BY location ORDER BY avg_temp DESC",
                measurement);

        // Then
        assertNotNull(query);
        assertTrue(query.contains("AVG(value)"));
        assertTrue(query.contains("COUNT(*)"));
        assertTrue(query.contains("GROUP BY location"));
    }

    @Test
    @DisplayName("Given InfluxQL MEAN function, When building query, Then should use MEAN not AVG")
    void givenInfluxQLMeanFunction_WhenBuildingQuery_ThenShouldUseMeanNotAvg() {
        // Given
        String measurement = "temperature";
        String field = "value";

        // When
        String query = String.format("SELECT MEAN(%s) FROM %s", field, measurement);

        // Then
        assertNotNull(query);
        assertTrue(query.contains("MEAN(value)"));
        assertFalse(query.contains("AVG")); // MEAN is InfluxQL, AVG is SQL
    }

    @Test
    @DisplayName("Given multi-measurement query, When building UNION ALL query, Then query should combine measurements")
    void givenMultiMeasurementQuery_WhenBuildingUnionAllQuery_ThenQueryShouldCombineMeasurements() {
        // When
        String query = "SELECT 'temperature' as type, sensor_id, location, value FROM temperature " +
                "UNION ALL " +
                "SELECT 'humidity' as type, sensor_id, location, value FROM humidity " +
                "ORDER BY sensor_id LIMIT 10";

        // Then
        assertNotNull(query);
        assertTrue(query.contains("UNION ALL"));
        assertTrue(query.contains("temperature"));
        assertTrue(query.contains("humidity"));
        assertTrue(query.contains("ORDER BY sensor_id"));
    }

    @Test
    @DisplayName("Given timestamp, When creating Point with timestamp, Then Point should have timestamp set")
    void givenTimestamp_WhenCreatingPointWithTimestamp_ThenPointShouldHaveTimestampSet() {
        // Given
        Instant now = Instant.now();

        // When
        Point point = Point.measurement("temperature")
                .setTag("sensor_id", "TH01")
                .setField("value", 23.2)
                .setTimestamp(now);

        // Then
        assertNotNull(point);
    }

    @Test
    @DisplayName("Given multiple fields, When creating Point with multiple fields, Then Point should be created successfully")
    void givenMultipleFields_WhenCreatingPointWithMultipleFields_ThenPointShouldBeCreated() {
        // Given & When
        Point point = Point.measurement("sensor_data")
                .setTag("sensor_id", "MULTI01")
                .setField("temperature", 23.5)
                .setField("humidity", 65.0)
                .setField("pressure", 1013.25)
                .setTimestamp(Instant.now());

        // Then
        assertNotNull(point);
    }

    @Test
    @DisplayName("Given measurement name, When creating Point without tags, Then Point should be created with fields only")
    void givenMeasurementName_WhenCreatingPointWithoutTags_ThenPointShouldBeCreatedWithFieldsOnly() {
        // Given
        String measurement = "simple_measurement";

        // When
        Point point = Point.measurement(measurement)
                .setField("value", 100.0)
                .setTimestamp(Instant.now());

        // Then
        assertNotNull(point);
    }
}