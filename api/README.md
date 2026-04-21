# AI Store Manage API

AI Store Management System - Backend API

## Tech Stack

- **Java**: 21
- **Framework**: Spring Boot 3.4.5
- **Build Tool**: Maven

## Quick Start

### Prerequisites

- JDK 21+
- Maven 3.9+

### Run

```bash
cd api
mvn spring-boot:run
```

### Verify

```bash
# Custom health check
curl http://localhost:8080/ping

# Actuator health
curl http://localhost:8080/actuator/health
```

## Project Structure

```
api/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/aistore/
│   │   │   ├── AiStoreManageApplication.java   # Entry point
│   │   │   └── controller/
│   │   │       └── HealthController.java        # Health check
│   │   └── resources/
│   │       └── application.yml                  # Configuration
│   └── test/
│       └── java/com/aistore/
│           └── AiStoreManageApplicationTests.java
└── .gitignore
```
