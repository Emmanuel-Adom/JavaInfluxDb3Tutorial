# Getting Started with Java and InfluxDB 3

A simple Java console application demonstrating how to connect, write, and query time-series data using InfluxDB 3.0.

## Overview

This project shows you how to build a Java console application that integrates with InfluxDB 3. The application demonstrates writing sensor data (temperature, humidity, pressure) and querying it using both SQL and InfluxQL.

## Features

- Separated concerns (Config, Service, App layers)
- Multiple write methods (Point API, Line Protocol, Batch operations)
- SQL and InfluxQL query examples
- Parametrized queries 
- Aggregation and multi-measurement queries
- Unit tests 

## Prerequisites

- Java 11 or higher
- Maven 1.4.0 
- InfluxDB Cloud account (or self-hosted instance)
- InfluxDB API token 

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/influxdb3-java-tutorial.git
cd influxdb3-java-tutorial
```

### 2. Configure InfluxDB Credentials

Copy the template and add your credentials:

```bash
cp src/main/resources/application.properties.template src/main/resources/application.properties
```

Rename `application.properties.template` file to `application.properties`:

```properties
INFLUXDB_HOST=https://your-region.aws.cloud2.influxdata.com
INFLUXDB_DATABASE=your-database-name
INFLUXDB_TOKEN=your-api-token-here
```

### 3. Build the Project

```bash
mvn clean compile
```

### 4. Run Tests

```bash
mvn test
```

## Running the Application

### Using Maven (with dependencies)

```bash
# Windows
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.App" -Dexec.args="--add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED"

# Mac/Linux
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.App" -Dexec.args="--add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED"
```

### Why the JVM Arguments?

InfluxDB 3 uses Apache Arrow, which requires access to Java's internal modules. The `--add-opens` flags grant this access.

### Alternative Shortcut (Works on all platforms)

```Bash
mvn clean compile exec:java
```
This works if the main class is already configured in the pom.xml file as done in this project.
Maven handles the JVM arguments automatically when it detects Apache Arrow.

## What the App Does

1. **Loads Configuration** - Reads credentials from `application.properties`
2. **Connects to InfluxDB** - Establishes a client connection
3. **Writes Sample Data** - Demonstrates three write methods:
    - Point API 
    - Line Protocol 
    - Batch operations 
4. **Queries Data** - Shows various query patterns:
    - Basic SQL queries
    - Parametrized queries
    - Aggregations
    - Multi-measurement queries
    - InfluxQL queries
    

## Project structure

The application consists of three main components:

- **ConfigurationManager**: Handles loading and validating credentials
- **InfluxDBService**: Encapsulates all database operations
- **App**: Orchestrates the workflow

This separation makes the code testable, maintainable, and easy to extend.

## Testing

Run all tests:

```bash
mvn test
```

Run specific test class:

```bash
mvn test -Dtest=ConfigurationManagerTest
```

## Technologies Used

- Java 21
- Maven 3.14.0
- InfluxDB 3.0 Java Client 1.4.0
- JUnit 5.13.4

## Resources

- [InfluxDB 3.0 Documentation](https://docs.influxdata.com/influxdb/cloud-serverless/)
- [Java Client Library](https://github.com/InfluxCommunity/influxdb3-java)
- [Article: Getting Started with Java and InfluxDB]()

---
