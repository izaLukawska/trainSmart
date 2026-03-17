# Trainsmart

An intelligent training platform to purchase training plans generated based on user's preferences
and current injuries and/or medical contraindications.

## 📑 Table of Contents

1. [Key Features](#-key-features)
2. [Technology Stack](#-technology-stack)
3. [Design Decisions & Architectural Choices](#-design-decisions--architectural-choices)
    - [Modular Monolith Architecture](#1-modular-monolith-architecture)
    - [Strategy Pattern for Exercise Logic](#2-strategy-pattern-for-exercise-logic)
    - [Containerized Integration Testing (Testcontainers)](#3-containerized-integration-testing-testcontainers)
    - [Robust Test Data Management (TestFixtureBuilder)](#4-robust-test-data-management-testfixturebuilder)
    - [Automated Quality Gates (CI/CD)](#5-automated-quality-gates-cicd)
4. [Quick Start](#quick-start)
    - [Prerequisites](#1-prerequisites)
    - [Environment Configuration](#2-environment-configuration)
    - [Running the App](#3-running-the-app)
        - [Fast Setup](#fast-setup)
        - [Full Setup (Docker)](#-full-setup-docker)

## 🌟 Key Features

- **Smart Training Plan Generation:** Generates workouts based on training type, volume, duration,
- and injury constraints via OpenAI.
- **Gym Finder:** Nearby gym search via OpenStreetMap, optimized with Redis caching for performance.
- **Automated Reporting:** Training plans generated in PDF or Excel formats for download or email delivery.

## 🛠 Technology Stack

* **Core:** Java 21, Spring Boot 3.5.6 (Modular Monolith), Lombok.
* **Security:** Spring Security + JWT.
* **Data & Cache:** PostgreSQL (Primary), H2 (Local), Redis, Spring Data JPA / Hibernate.
* **Integrations:** OpenAI API, OpenStreetMap API, Apache POI, iTextPDF.
* **Quality & DevOps:** JUnit 5, Mockito, **Testcontainers**, **JaCoCo (80%+ coverage)**, GitHub Actions, Maven.
* **API Docs:** Swagger / OpenAPI.

## 🏗 Design Decisions & Architectural Choices

### 1. Modular Monolith Architecture

**Choice:** A single-deployment artifact structured into independent, domain-driven modules rather than a distributed
microservices network.

**Reasoning:**

- **Clear boundaries:** Each module has a distinct responsibility.
- **No microservice overhead:** Avoids the operational complexity of running multiple services.
- **Future-proof:** Flexibility to switch to microservices later if needed.

### 2. Strategy Pattern for Exercise Logic

**Choice:** Implementation of the Strategy Pattern to dynamically determine exercise activation and filtering logic.

**Reasoning:** Simplifies complex conditional logic and allows selecting the right strategy at runtime to:

- **Reduce costs:** Avoid unnecessary OpenAI calls for users without new injuries.
- **Optimize performance:** Only send relevant exercises to the AI.
- **Improve maintainability:** Remove nested `if`-`else` blocks, making the flow easier to test and extend.

### 3. Containerized Integration Testing (Testcontainers)

Ensures tests run with real databases (PostgreSQL and Redis) so they match production behavior.

### 4. Robust Test Data Management (TestFixtureBuilder)

**Choice:** Implement custom Fixture Builders to automate test data creation.

**Reasoning:** Reduces manual setup, minimizing boilerplate and enabling faster, more reliable test preparation.

### 5. Automated Quality Gates (CI/CD)

**Choice:** GitHub Actions pipeline with automated health checks and JaCoCo coverage enforcement.

**Reasoning:** Ensures that no code is merged without passing the 80% coverage threshold
and basic "smoke tests" (verifying the Spring context starts successfully in a Docker container).

## Quick Start

This project can be run in **two ways**:

1. **Full Setup** – runs locally with postgreSQL
2. **Fast Setup** – runs fully in-memory with H2

### 1. Prerequisites

* JDK 21 & Maven 3.9+
* Docker (for Testcontainers and local infrastructure)

### 2. Environment Configuration

The application requires the following environment variables to be set (see `application.yml` for defaults):

| Variable        | Description              | Default / Example            |
|:----------------|:-------------------------|:-----------------------------|
| `DB_HOST`       | PostgreSQL Host          | `localhost`                  |
| `DB_PORT`       | PostgreSQL Port          | `5432`                       |
| `DB_NAME`       | Database Name            | `trainsmart_db`              |
| `DB_USER`       | Database Username        | `trainsmart_user`            |
| `DB_PASSWORD`   | Database Password        | `password`                   |
| `REDIS_HOST`    | Redis Server Host        | `localhost`                  |
| `REDIS_PORT`    | Redis Server Port        | `6379`                       |
| `OPEN_AI_KEY`   | OpenAI API Secret Key    | -                            |
| `JWT_SECRET`    | Secret for Token Signing | (Min. 32 chars)              |
| `MAIL_HOST`     | SMTP Server Host         | `localhost` (Mailtrap/Gmail) |
| `MAIL_USERNAME` | SMTP Username            | `user@gmail.com`             |
| `MAIL_PASSWORD` | SMTP Password            | `password`                   |

### 3. Running the app

```bash
# Clone the repo
git clone https://github.com/izaLukawska/trainSmart.git
````

#### Fast Setup

- Runs in-memory using H2 + MailHog
- Minimal configuration, no Postgres or Redis required
- OpenAI features **won’t fully work**; injuries/medical restrictions are bypassed

**Steps:**

```bash
# Go into the app directory
cd trainSmart/app

# Run the application
mvn spring-boot:run -Dspring-boot.run.profiles=fast
```

#### 🐳 Full Setup (Docker)

**Steps:**

```bash
# Build the app
mvn clean package

# Run with Docker Compose
docker compose up --build
```
