# Employee Management System — API Documentation

## Base URL

```
http://localhost:8080
```

All API endpoints are prefixed with `/api`. The application uses an **H2 in-memory database** pre-populated with sample data on startup.

---

## Table of Contents

1. [Employee APIs](#1-employee-apis)
2. [Department APIs](#2-department-apis)
3. [Error Responses](#3-error-responses)
4. [Data Models](#4-data-models)
5. [Additional Resources](#5-additional-resources)
6. [Deployment](#6-deployment)
7. [CI/CD Pipelines](#7-cicd-pipelines)

---

## 1. Employee APIs

Base path: `/api/employees`

### 1.1 Get All Employees

Retrieve a list of all employees.

**Request**

```
GET /api/employees
```

```bash
curl -X GET http://localhost:8080/api/employees
```

**Response** `200 OK`

```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "123-456-7890",
    "position": "Senior Software Engineer",
    "salary": 95000.00,
    "department": {
      "id": 1,
      "departmentName": "Engineering",
      "departmentLocation": "Building A",
      "departmentDescription": "Software Development and Engineering"
    }
  },
  {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "phone": "234-567-8901",
    "position": "Software Engineer",
    "salary": 85000.00,
    "department": {
      "id": 1,
      "departmentName": "Engineering",
      "departmentLocation": "Building A",
      "departmentDescription": "Software Development and Engineering"
    }
  }
]
```

---

### 1.2 Get Employee by ID

Retrieve a single employee by their unique ID.

**Request**

```
GET /api/employees/{id}
```

```bash
curl -X GET http://localhost:8080/api/employees/1
```

**Response** `200 OK`

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "123-456-7890",
  "position": "Senior Software Engineer",
  "salary": 95000.00,
  "department": {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development and Engineering"
  }
}
```

**Error Response** `404 Not Found`

```bash
curl -X GET http://localhost:8080/api/employees/9999
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 9999"
}
```

---

### 1.3 Get Employee by Email

Retrieve an employee by their email address.

**Request**

```
GET /api/employees/search/by-email?email={email}
```

```bash
curl -X GET "http://localhost:8080/api/employees/search/by-email?email=john.doe@example.com"
```

**Response** `200 OK`

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "123-456-7890",
  "position": "Senior Software Engineer",
  "salary": 95000.00,
  "department": {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development and Engineering"
  }
}
```

**Error Response** `404 Not Found`

```bash
curl -X GET "http://localhost:8080/api/employees/search/by-email?email=unknown@example.com"
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with email: unknown@example.com"
}
```

---

### 1.4 Get Employees by Department

Retrieve all employees belonging to a specific department.

**Request**

```
GET /api/employees/department/{departmentId}
```

```bash
curl -X GET http://localhost:8080/api/employees/department/1
```

**Response** `200 OK`

```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "123-456-7890",
    "position": "Senior Software Engineer",
    "salary": 95000.00,
    "department": {
      "id": 1,
      "departmentName": "Engineering",
      "departmentLocation": "Building A",
      "departmentDescription": "Software Development and Engineering"
    }
  },
  {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "phone": "234-567-8901",
    "position": "Software Engineer",
    "salary": 85000.00,
    "department": {
      "id": 1,
      "departmentName": "Engineering",
      "departmentLocation": "Building A",
      "departmentDescription": "Software Development and Engineering"
    }
  },
  {
    "id": 3,
    "firstName": "Bob",
    "lastName": "Johnson",
    "email": "bob.johnson@example.com",
    "phone": "345-678-9012",
    "position": "DevOps Engineer",
    "salary": 90000.00,
    "department": {
      "id": 1,
      "departmentName": "Engineering",
      "departmentLocation": "Building A",
      "departmentDescription": "Software Development and Engineering"
    }
  },
  {
    "id": 4,
    "firstName": "Alice",
    "lastName": "Williams",
    "email": "alice.williams@example.com",
    "phone": "456-789-0123",
    "position": "Engineering Manager",
    "salary": 120000.00,
    "department": {
      "id": 1,
      "departmentName": "Engineering",
      "departmentLocation": "Building A",
      "departmentDescription": "Software Development and Engineering"
    }
  }
]
```

---

### 1.5 Create a New Employee

Create a new employee record.

**Request**

```
POST /api/employees
Content-Type: application/json
```

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Sarah",
    "lastName": "Connor",
    "email": "sarah.connor@example.com",
    "phone": "666-777-8888",
    "position": "QA Engineer",
    "salary": 80000.00,
    "department": {
      "id": 1
    }
  }'
```

**Response** `201 Created`

```json
{
  "id": 16,
  "firstName": "Sarah",
  "lastName": "Connor",
  "email": "sarah.connor@example.com",
  "phone": "666-777-8888",
  "position": "QA Engineer",
  "salary": 80000.00,
  "department": {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development and Engineering"
  }
}
```

**Error Response** `400 Bad Request` — Duplicate Email

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Duplicate",
    "lastName": "User",
    "email": "john.doe@example.com",
    "phone": "000-000-0000",
    "position": "Tester",
    "salary": 60000.00,
    "department": {
      "id": 1
    }
  }'
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Employee with email john.doe@example.com already exists"
}
```

---

### 1.6 Update an Employee

Update an existing employee's information.

**Request**

```
PUT /api/employees/{id}
Content-Type: application/json
```

```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "999-888-7777",
    "position": "Lead Software Engineer",
    "salary": 110000.00,
    "department": {
      "id": 1
    }
  }'
```

**Response** `200 OK`

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "999-888-7777",
  "position": "Lead Software Engineer",
  "salary": 110000.00,
  "department": {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development and Engineering"
  }
}
```

**Error Response** `404 Not Found`

```bash
curl -X PUT http://localhost:8080/api/employees/9999 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ghost",
    "lastName": "User",
    "email": "ghost@example.com",
    "phone": "000-000-0000",
    "position": "None",
    "salary": 0.00,
    "department": {
      "id": 1
    }
  }'
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 9999"
}
```

---

### 1.7 Delete an Employee

Delete an employee by their ID.

**Request**

```
DELETE /api/employees/{id}
```

```bash
curl -X DELETE http://localhost:8080/api/employees/16
```

**Response** `204 No Content`

*(Empty response body on success)*

**Error Response** `404 Not Found`

```bash
curl -X DELETE http://localhost:8080/api/employees/9999
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 9999"
}
```

---

### 1.8 Search Employees with Pagination and External API

Search employees by keyword (first name, last name, or email) with pagination and sorting, while simultaneously calling an external API.

**Request**

```
GET /api/employees/search?keyword={keyword}&page={page}&size={size}&sortBy={sortBy}&direction={direction}&externalApiUrl={url}
```

**Parameters**

| Parameter        | Type   | Required | Default                  | Description                                  |
|------------------|--------|----------|--------------------------|----------------------------------------------|
| `keyword`        | String | Yes      | —                        | Search keyword for first name, last name, or email |
| `page`           | int    | No       | 0                        | Page number (0-based)                        |
| `size`           | int    | No       | 10                       | Page size                                    |
| `sortBy`         | String | No       | firstName                | Field to sort by                             |
| `direction`      | String | No       | ASC                      | Sort direction: ASC or DESC                  |
| `externalApiUrl` | String | No       | https://www.google.com   | External API URL to call                     |

```bash
curl -X GET "http://localhost:8080/api/employees/search?keyword=John&page=0&size=5&sortBy=firstName&direction=ASC&externalApiUrl=https://www.google.com"
```

**Response** `200 OK`

```json
{
  "employees": {
    "content": [
      {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "phone": "123-456-7890",
        "position": "Senior Software Engineer",
        "salary": 95000.00,
        "department": {
          "id": 1,
          "departmentName": "Engineering",
          "departmentLocation": "Building A",
          "departmentDescription": "Software Development and Engineering"
        }
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 5,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      }
    },
    "totalPages": 1,
    "totalElements": 1,
    "last": true,
    "first": true,
    "size": 5,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 1,
    "empty": false
  },
  "externalApiData": {
    "status": "success",
    "contentLength": 15234,
    "preview": "<!doctype html><html itemscope..."
  },
  "externalApiUrl": "https://www.google.com",
  "externalApiSuccess": true
}
```

**Response with External API Error**

If the external API call fails, the employee search results are still returned, but the external API data will contain error information:

```json
{
  "employees": {
    "content": [],
    "totalPages": 0,
    "totalElements": 0,
    "last": true,
    "first": true,
    "size": 10,
    "number": 0,
    "numberOfElements": 0,
    "empty": true
  },
  "externalApiData": {
    "status": "error",
    "message": "Connection refused: https://invalid.url"
  },
  "externalApiUrl": "https://invalid.url",
  "externalApiSuccess": false
}
```

**Notes**

- The search is case-insensitive and matches against `firstName`, `lastName`, and `email` fields.
- The external API call does not block the employee search results — even if the external API fails, the employee results are still returned.
- The external API response preview is limited to 200 characters.

---

## 2. Department APIs

Base path: `/api/departments`

### 2.1 Get All Departments

Retrieve a list of all departments.

**Request**

```
GET /api/departments
```

```bash
curl -X GET http://localhost:8080/api/departments
```

**Response** `200 OK`

```json
[
  {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development and Engineering"
  },
  {
    "id": 2,
    "departmentName": "Marketing",
    "departmentLocation": "Building B",
    "departmentDescription": "Marketing and Brand Management"
  },
  {
    "id": 3,
    "departmentName": "Human Resources",
    "departmentLocation": "Building C",
    "departmentDescription": "Human Resources and Recruitment"
  },
  {
    "id": 4,
    "departmentName": "Finance",
    "departmentLocation": "Building D",
    "departmentDescription": "Finance and Accounting"
  },
  {
    "id": 5,
    "departmentName": "Sales",
    "departmentLocation": "Building E",
    "departmentDescription": "Sales and Business Development"
  }
]
```

---

### 2.2 Get Department by ID

Retrieve a single department by its unique ID.

**Request**

```
GET /api/departments/{id}
```

```bash
curl -X GET http://localhost:8080/api/departments/1
```

**Response** `200 OK`

```json
{
  "id": 1,
  "departmentName": "Engineering",
  "departmentLocation": "Building A",
  "departmentDescription": "Software Development and Engineering"
}
```

**Error Response** `404 Not Found`

```bash
curl -X GET http://localhost:8080/api/departments/9999
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Department not found with id: 9999"
}
```

---

### 2.3 Get Department by Name

Retrieve a department by its name.

**Request**

```
GET /api/departments/search/by-name?departmentName={name}
```

```bash
curl -X GET "http://localhost:8080/api/departments/search/by-name?departmentName=Engineering"
```

**Response** `200 OK`

```json
{
  "id": 1,
  "departmentName": "Engineering",
  "departmentLocation": "Building A",
  "departmentDescription": "Software Development and Engineering"
}
```

**Error Response** `404 Not Found`

```bash
curl -X GET "http://localhost:8080/api/departments/search/by-name?departmentName=Nonexistent"
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Department not found with name: Nonexistent"
}
```

---

### 2.4 Create a New Department

Create a new department record.

**Request**

```
POST /api/departments
Content-Type: application/json
```

```bash
curl -X POST http://localhost:8080/api/departments \
  -H "Content-Type: application/json" \
  -d '{
    "departmentName": "Research and Development",
    "departmentLocation": "Building F",
    "departmentDescription": "Research and Product Innovation"
  }'
```

**Response** `201 Created`

```json
{
  "id": 6,
  "departmentName": "Research and Development",
  "departmentLocation": "Building F",
  "departmentDescription": "Research and Product Innovation"
}
```

**Error Response** `400 Bad Request` — Duplicate Name

```bash
curl -X POST http://localhost:8080/api/departments \
  -H "Content-Type: application/json" \
  -d '{
    "departmentName": "Engineering",
    "departmentLocation": "Building G",
    "departmentDescription": "Duplicate"
  }'
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Department with name Engineering already exists"
}
```

---

### 2.5 Update a Department

Update an existing department's information.

**Request**

```
PUT /api/departments/{id}
Content-Type: application/json
```

```bash
curl -X PUT http://localhost:8080/api/departments/1 \
  -H "Content-Type: application/json" \
  -d '{
    "departmentName": "Engineering",
    "departmentLocation": "Building A - Floor 3",
    "departmentDescription": "Software Development, Engineering, and Infrastructure"
  }'
```

**Response** `200 OK`

```json
{
  "id": 1,
  "departmentName": "Engineering",
  "departmentLocation": "Building A - Floor 3",
  "departmentDescription": "Software Development, Engineering, and Infrastructure"
}
```

**Error Response** `404 Not Found`

```bash
curl -X PUT http://localhost:8080/api/departments/9999 \
  -H "Content-Type: application/json" \
  -d '{
    "departmentName": "Ghost",
    "departmentLocation": "Nowhere",
    "departmentDescription": "Does not exist"
  }'
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Department not found with id: 9999"
}
```

---

### 2.6 Delete a Department

Delete a department by its ID.

**Request**

```
DELETE /api/departments/{id}
```

```bash
curl -X DELETE http://localhost:8080/api/departments/6
```

**Response** `204 No Content`

*(Empty response body on success)*

**Error Response** `404 Not Found`

```bash
curl -X DELETE http://localhost:8080/api/departments/9999
```

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Department not found with id: 9999"
}
```

---

## 3. Error Responses

All error responses follow a consistent format handled by the `GlobalExceptionHandler`.

### 3.1 404 Not Found

Returned when a requested resource does not exist.

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee not found with id: 9999"
}
```

### 3.2 400 Bad Request

Returned when the request contains invalid data (e.g., duplicate email or department name).

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Employee with email john.doe@example.com already exists"
}
```

### 3.3 500 Internal Server Error

Returned when an unexpected server error occurs.

```json
{
  "timestamp": "2026-10-12T17:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Unexpected error message"
}
```

---

## 4. Data Models

### 4.1 Employee

| Field             | Type     | Required | Description                        |
|-------------------|----------|----------|------------------------------------|
| `id`              | Long     | Auto     | Unique identifier (auto-generated) |
| `firstName`       | String   | Yes      | Employee's first name (max 50)     |
| `lastName`        | String   | Yes      | Employee's last name (max 50)      |
| `email`           | String   | Yes      | Unique email address (max 100)     |
| `phone`           | String   | No       | Phone number (max 20)              |
| `position`        | String   | No       | Job title (max 100)                |
| `salary`          | Double   | No       | Annual salary                      |
| `department`      | Object   | Yes      | Department reference (by `id`)     |

### 4.2 Department

| Field                    | Type     | Required | Description                          |
|--------------------------|----------|----------|--------------------------------------|
| `id`                     | Long     | Auto     | Unique identifier (auto-generated)   |
| `departmentName`         | String   | Yes      | Department name (max 100)            |
| `departmentLocation`     | String   | No       | Physical location (max 200)          |
| `departmentDescription`  | String   | No       | Description (max 500)                |

---

## 5. Additional Resources

### 5.1 Swagger / OpenAPI UI

Interactive API documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI spec JSON:

```
http://localhost:8080/api-docs
```

### 5.2 H2 Database Console

The H2 console is enabled for direct database access:

```
http://localhost:8080/h2-console
```

**Connection settings:**

| Setting            | Value                      |
|--------------------|----------------------------|
| JDBC URL           | `jdbc:h2:mem:employeedb`  |
| Username           | `sa`                       |
| Password           | *(empty)*                  |

### 5.3 Sample Data

The application starts with **5 departments** and **15 employees** pre-loaded:

| Department        | Employee Count |
|-------------------|----------------|
| Engineering       | 4              |
| Marketing         | 3              |
| Human Resources   | 2              |
| Finance           | 3              |
| Sales             | 3              |

---

## Quick Reference — All Endpoints

| Method   | Endpoint                                      | Description                    |
|----------|-----------------------------------------------|--------------------------------|
| `GET`    | `/api/employees`                              | Get all employees              |
| `GET`    | `/api/employees/{id}`                         | Get employee by ID             |
| `GET`    | `/api/employees/search/by-email?email={email}`| Get employee by email          |
| `GET`    | `/api/employees/department/{departmentId}`    | Get employees by department    |
| `GET`    | `/api/employees/search?keyword={keyword}`     | Search employees with pagination and external API |
| `POST`   | `/api/employees`                              | Create a new employee          |
| `PUT`    | `/api/employees/{id}`                         | Update an employee             |
| `DELETE` | `/api/employees/{id}`                         | Delete an employee             |
| `GET`    | `/api/departments`                            | Get all departments            |
| `GET`    | `/api/departments/{id}`                       | Get department by ID           |
| `GET`    | `/api/departments/search/by-name?departmentName={name}` | Get department by name |
| `POST`   | `/api/departments`                            | Create a new department        |
| `PUT`    | `/api/departments/{id}`                       | Update a department            |
| `DELETE` | `/api/departments/{id}`                       | Delete a department            |

---

## 6. Deployment

This section covers containerizing and deploying the Employee Management System using Docker, Docker Compose, and Kubernetes.

### 6.1 Docker

The application includes a multi-stage Dockerfile optimized for production deployments.

#### Dockerfile Features

- **Multi-stage build**: Separate build and production stages to minimize image size.
- **Layer caching**: `pom.xml` is copied and dependencies are downloaded (`mvn dependency:go-offline`) before copying source code, so dependency layers are cached between builds.
- **Non-root user**: Runs as `spring` user for security.
- **Health check**: Built-in `HEALTHCHECK` directive using `wget` to check `/actuator/health`.
- **JVM optimization**: Container-aware JVM flags (`UseContainerSupport`, `MaxRAMPercentage=75.0`, `G1GC`, `MaxGCPauseMillis=200`).

#### Build the Docker Image

```bash
docker build -t employeeapp:latest .
```

#### Run the Container (with H2 in-memory database)

```bash
docker run -d -p 8080:8080 --name employee-app employeeapp:latest
```

#### Access the Application

- **API**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console`

#### Stop and Remove the Container

```bash
docker stop employee-app
docker rm employee-app
```

#### Check Container Health

```bash
docker inspect --format='{{.State.Health.Status}}' employee-app
```

---

### 6.2 Docker Compose

Docker Compose provides a complete local development environment with MySQL database and phpMyAdmin for database management.

#### Services

| Service       | Image          | Port  | Description                          |
|---------------|----------------|-------|--------------------------------------|
| `employeeapp` | Custom build   | 8080  | Spring Boot application              |
| `mysql`       | mysql:8.0      | 3306  | MySQL 8.0 database                   |
| `phpmyadmin`  | phpmyadmin     | 8081  | Web-based database management UI     |

#### Start the Environment

```bash
docker-compose up -d
```

This command starts all three services in detached mode. The application waits for MySQL to be ready before starting.

#### Environment Variables

The following environment variables are configured in `docker-compose.yml`:

**MySQL Service:**

| Variable              | Value             | Description              |
|-----------------------|-------------------|--------------------------|
| `MYSQL_ROOT_PASSWORD` | `rootpassword123` | Root password for MySQL  |
| `MYSQL_DATABASE`      | `employeedb`      | Database name            |
| `MYSQL_USER`          | `root`            | MySQL user               |
| `MYSQL_PASSWORD`      | `rootpassword123` | MySQL user password      |

**Application Service:**

| Variable               | Value        | Description                      |
|------------------------|--------------|----------------------------------|
| `SPRING_PROFILES_ACTIVE` | `prod`     | Activates production profile     |
| `DB_HOST`              | `mysql`      | MySQL hostname (service name)    |
| `DB_PORT`              | `3306`       | MySQL port                       |
| `DB_NAME`              | `employeedb` | Database name                    |
| `DB_USERNAME`          | `root`       | Database username                |
| `DB_PASSWORD`          | `rootpassword123` | Database password           |

#### Access the Services

- **API**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **phpMyAdmin**: `http://localhost:8081`
  - **Server**: `mysql`
  - **Username**: `root`
  - **Password**: `rootpassword123`

#### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f employeeapp
docker-compose logs -f mysql
```

#### Stop the Environment

```bash
# Stop containers (preserve data volumes)
docker-compose down

# Stop containers and remove data volumes (deletes all data)
docker-compose down -v
```

#### Health Checks

Both the MySQL and application services include health checks:

- **MySQL**: Checks connectivity using `mysqladmin ping`.
- **Application**: Checks `/actuator/health` endpoint via HTTP.

```bash
# Check health status
docker inspect --format='{{.State.Health.Status}}' employeeapp-mysql
docker inspect --format='{{.State.Health.Status}}' employeeapp-app
```

---

### 6.3 Kubernetes Deployment

The `kubernetes/` directory contains production-ready Kubernetes manifests for deploying the application to any Kubernetes cluster.

#### Manifest Overview

| File                  | Kind            | Description                              |
|-----------------------|-----------------|------------------------------------------|
| `namespace.yaml`      | Namespace       | Isolated `employeeapp` namespace         |
| `secret.yaml`         | Secret          | Database credentials (Opaque secret)     |
| `configmap.yaml`      | ConfigMap       | Application configuration                |
| `mysql-pvc.yaml`      | PersistentVolumeClaim | 5Gi persistent storage for MySQL   |
| `mysql-deployment.yaml` | Deployment    | MySQL 8.0 database with health probes    |
| `mysql-service.yaml`  | Service         | ClusterIP service for MySQL (port 3306)  |
| `deployment.yaml`     | Deployment      | Application with 3 replicas and rolling updates |
| `service.yaml`        | Service         | ClusterIP service for the app (port 80)  |
| `ingress.yaml`        | Ingress         | NGINX Ingress with TLS support           |
| `hpa.yaml`            | HorizontalPodAutoscaler | Auto-scaling (2–10 replicas)   |

#### Prerequisites

- Kubernetes cluster (minikube, kind, EKS, GKE, AKS, etc.)
- `kubectl` configured to access the cluster
- NGINX Ingress Controller installed (for Ingress)
- Metrics Server installed (for HPA)
- Docker image pushed to a container registry (for production)

#### Quick Start

```bash
# Build the Docker image
docker build -t employeeapp:latest .

# If using minikube, load the image
minikube image build -t employeeapp:latest .

# Deploy all resources
kubectl apply -f kubernetes/

# Verify deployment
kubectl get pods -n employeeapp
kubectl get svc -n employeeapp
kubectl get ingress -n employeeapp
```

#### Deployment Resources

**Application Deployment:**

| Setting              | Value                              |
|----------------------|------------------------------------|
| Replicas             | 3                                  |
| Strategy             | RollingUpdate (maxSurge: 1, maxUnavailable: 0) |
| CPU Request          | 250m                               |
| CPU Limit            | 500m                               |
| Memory Request       | 512Mi                              |
| Memory Limit         | 1Gi                                |
| Liveness Probe       | `/actuator/health/liveness` (initialDelay: 40s, period: 10s) |
| Readiness Probe      | `/actuator/health/readiness` (initialDelay: 30s, period: 5s) |
| Startup Probe        | `/actuator/health` (initialDelay: 10s, period: 5s, failureThreshold: 10) |

**MySQL Deployment:**

| Setting              | Value                              |
|----------------------|------------------------------------|
| Replicas             | 1                                  |
| CPU Request          | 250m                               |
| CPU Limit            | 500m                               |
| Memory Request       | 512Mi                              |
| Memory Limit         | 1Gi                                |
| Storage              | 5Gi (PVC)                          |
| Liveness Probe       | `mysqladmin ping` (initialDelay: 30s) |
| Readiness Probe      | `SELECT 1` (initialDelay: 10s)     |

**Horizontal Pod Autoscaler:**

| Setting              | Value                              |
|----------------------|------------------------------------|
| Min Replicas         | 2                                  |
| Max Replicas         | 10                                 |
| CPU Target           | 70% utilization                    |
| Memory Target        | 80% utilization                    |
| Scale Up Stabilization | 60 seconds                       |
| Scale Down Stabilization | 300 seconds                    |

#### Ingress Configuration

The Ingress is configured for NGINX with the following annotations:

| Annotation                              | Value    | Description                      |
|-----------------------------------------|----------|----------------------------------|
| `nginx.ingress.kubernetes.io/rewrite-target` | `/`    | Rewrite all paths to root        |
| `nginx.ingress.kubernetes.io/ssl-redirect` | `"true"` | Redirect HTTP to HTTPS          |
| `nginx.ingress.kubernetes.io/proxy-body-size` | `"10m"` | Max request body size           |
| `nginx.ingress.kubernetes.io/proxy-read-timeout` | `"60"` | Read timeout in seconds         |
| `nginx.ingress.kubernetes.io/proxy-send-timeout` | `"60"` | Send timeout in seconds         |

**TLS Configuration:**

The Ingress expects a TLS secret named `employeeapp-tls-secret` for the host `employeeapp.example.com`. To configure TLS:

```bash
# Generate a self-signed certificate (for development)
kubectl create secret tls employeeapp-tls-secret \
  --cert=tls.crt --key=tls.key -n employeeapp

# Or use cert-manager for automatic certificate management
```

#### Access the Application

```bash
# Port forward for local access
kubectl port-forward svc/employeeapp 8080:80 -n employeeapp

# Access the API
curl http://localhost:8080/api/employees

# Access Swagger UI
open http://localhost:8080/swagger-ui.html
```

#### Monitoring and Scaling

```bash
# View pod status
kubectl get pods -n employeeapp

# View HPA status
kubectl get hpa -n employeeapp

# View logs
kubectl logs -f deployment/employeeapp -n employeeapp

# View resource usage
kubectl top pods -n employeeapp

# Manual scaling (overrides HPA temporarily)
kubectl scale deployment employeeapp --replicas=5 -n employeeapp
```

#### Production Checklist

- [ ] Update `kubernetes/secret.yaml` with strong, unique database credentials
- [ ] Configure TLS certificates for the Ingress (use cert-manager or a managed certificate service)
- [ ] Push Docker image to a container registry and update image references in `deployment.yaml`
- [ ] Set `imagePullPolicy: Always` in `deployment.yaml` for production
- [ ] Install Metrics Server for HPA to function
- [ ] Install NGINX Ingress Controller
- [ ] Configure proper `storageClassName` in `mysql-pvc.yaml` for your cluster
- [ ] Set up monitoring (Prometheus/Grafana) and logging (ELK/EFK)
- [ ] Configure network policies for pod-to-pod communication
- [ ] Review and adjust resource requests/limits based on load testing

#### Rollback

```bash
# Rollback to the previous deployment
kubectl rollout undo deployment/employeeapp -n employeeapp

# Check rollout status
kubectl rollout status deployment/employeeapp -n employeeapp

# View rollout history
kubectl rollout history deployment/employeeapp -n employeeapp
```

---

### 6.4 Production Profiles

The application includes pre-configured Spring profiles for different database environments.

#### Available Profiles

| Profile      | Database   | Properties File                  |
|--------------|------------|----------------------------------|
| `default`    | H2 (in-memory) | `application.properties`     |
| `prod`       | MySQL      | `application-prod.properties`    |

#### Activating a Profile

```bash
# Via command line
java -jar app.jar --spring.profiles.active=prod

# Via environment variable
export SPRING_PROFILES_ACTIVE=prod
java -jar app.jar

# Via Docker
docker run -d -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod employeeapp:latest

# Via Docker Compose (configured in docker-compose.yml)
docker-compose up -d
```

#### Production Profile Configuration

The `prod` profile (`application-prod.properties`) uses environment variables for database configuration:

| Environment Variable | Default      | Description              |
|----------------------|--------------|--------------------------|
| `DB_HOST`            | `localhost`  | Database host            |
| `DB_PORT`            | `3306`       | Database port            |
| `DB_NAME`            | `employeedb` | Database name            |
| `DB_USERNAME`        | `root`       | Database username        |
| `DB_PASSWORD`        | `root`       | Database password        |

#### Actuator Endpoints

In production profiles, Spring Boot Actuator health endpoints are exposed:

| Endpoint                    | Description                    |
|-----------------------------|--------------------------------|
| `GET /actuator/health`      | Overall health status          |
| `GET /actuator/health/liveness` | Liveness probe for Kubernetes |
| `GET /actuator/health/readiness` | Readiness probe for Kubernetes |

**Health Response Example:**

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "SELECT 1"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 100000000000,
        "free": 50000000000,
        "threshold": 10485760
      }
    }
  }
}
```

---

### 6.5 Architecture Diagrams

#### 6.5.1 Local Development Environment (H2 In-Memory)

```
┌─────────────────────────────────────────────────────────────────┐
│                    LOCAL DEVELOPMENT ENVIRONMENT                │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                  Developer Machine                       │   │
│  │                                                          │   │
│  │  ┌─────────────┐    ┌──────────────────┐                │   │
│  │  │   Browser    │───>│  Spring Boot     │                │   │
│  │  │  (Swagger UI)│    │  employeeapp     │                │   │
│  │  └─────────────┘    │  (Port 8080)     │                │   │
│  │                     └───────┬──────────┘                │   │
│  │                             │                           │   │
│  │                     ┌───────▼──────────┐                │   │
│  │                     │  H2 Database     │                │   │
│  │                     │  (In-Memory)     │                │   │
│  │                     │  (Embedded)      │                │   │
│  │                     └──────────────────┘                │   │
│  │                                                          │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘

Key Points:
- No external database required
- H2 console available at http://localhost:8080/h2-console
- Swagger UI at http://localhost:8080/swagger-ui.html
- Data is lost on application restart
```

#### 6.5.2 Docker Compose Environment (MySQL)

```
┌─────────────────────────────────────────────────────────────────┐
│                  DOCKER COMPOSE ENVIRONMENT                     │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                  Docker Network                          │   │
│  │                                                          │   │
│  │  ┌─────────────┐    ┌──────────────────┐    ┌────────┐  │   │
│  │  │   Browser    │───>│   employeeapp    │───>│ MySQL  │  │   │
│  │  │  (Port 8080) │    │   (Port 8080)   │    │ 8.0    │  │   │
│  │  └─────────────┘    │   Spring Boot    │    │(3306)  │  │   │
│  │                     └──────────────────┘    └───┬────┘  │   │
│  │                                                 │        │   │
│  │                                        ┌────────▼───────┐ │   │
│  │                                        │  mysql_data    │ │   │
│  │                                        │  (Volume)      │ │   │
│  │                                        └────────────────┘ │   │
│  │                                                          │   │
│  │  ┌─────────────┐    ┌──────────────────┐                │   │
│  │  │   Browser    │───>│  phpMyAdmin      │───>│ MySQL    │  │   │
│  │  │  (Port 8081) │    │  (Port 8081)    │    │ 8.0      │  │   │
│  │  └─────────────┘    └──────────────────┘    └─────────┘  │   │
│  │                                                          │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘

Services:
- employeeapp: Spring Boot application with prod profile
- MySQL 8.0: Persistent database with volume mount
- phpMyAdmin: Web-based database management tool

Health Checks:
- employeeapp: HTTP health check on /actuator/health
- MySQL: mysqladmin ping
```

#### 6.5.3 Kubernetes Production Environment

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    KUBERNETES PRODUCTION ENVIRONMENT                    │
│                                                                         │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │                     Internet / External Traffic                   │  │
│  └────────────────────────────┬──────────────────────────────────────┘  │
│                               │                                         │
│                    ┌──────────▼──────────┐                              │
│                    │   NGINX Ingress     │  (TLS termination)           │
│                    │   Controller        │  employeeapp.example.com     │
│                    └──────────┬──────────┘                              │
│                               │                                         │
│                    ┌──────────▼──────────┐                              │
│                    │    Ingress Resource │  /api -> employeeapp:80      │
│                    └──────────┬──────────┘                              │
│                               │                                         │
│                    ┌──────────▼──────────┐                              │
│                    │  ClusterIP Service  │  employeeapp:80              │
│                    │  (Load Balancing)   │  Round-robin                 │
│                    └──────────┬──────────┘                              │
│                               │                                         │
│          ┌────────────────────┼────────────────────┐                    │
│          │                    │                    │                    │
│  ┌───────▼────────┐  ┌───────▼────────┐  ┌───────▼────────┐           │
│  │   Replica 1    │  │   Replica 2    │  │   Replica 3    │           │
│  │   Pod          │  │   Pod          │  │   Pod          │           │
│  │   employeeapp  │  │   employeeapp  │  │   employeeapp  │           │
│  │   (Port 8080)  │  │   (Port 8080)  │  │   (Port 8080)  │           │
│  │   512Mi/500m   │  │   512Mi/500m   │  │   512Mi/500m   │           │
│  └───────┬────────┘  └───────┬────────┘  └───────┬────────┘           │
│          │                   │                    │                    │
│          └────────────────────┼────────────────────┘                    │
│                               │                                         │
│                    ┌──────────▼──────────┐                              │
│                    │  Horizontal Pod     │  2-10 replicas               │
│                    │  Autoscaler (HPA)   │  CPU: 70%, Memory: 80%       │
│                    └─────────────────────┘                              │
│                                                                         │
│                    ┌──────────────────────────────────────┐             │
│                    │  ClusterIP Service: mysql:3306       │             │
│                    └──────────────────┬───────────────────┘             │
│                                       │                                 │
│                    ┌──────────────────▼───────────────────┐             │
│                    │  MySQL 8.0 Pod                       │             │
│                    │  (1 replica, 1Gi/1000m)              │             │
│                    └──────────────────┬───────────────────┘             │
│                                       │                                 │
│                    ┌──────────────────▼───────────────────┐             │
│                    │  Persistent Volume Claim (5Gi)       │             │
│                    │  (mysql-pvc)                         │             │
│                    └──────────────────────────────────────┘             │
│                                                                         │
│  Configuration:                                                         │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐            │
│  │    Secret      │  │   ConfigMap    │  │   Namespace    │            │
│  │  (DB creds)    │  │  (App config)  │  │  (employeeapp) │            │
│  └────────────────┘  └────────────────┘  └────────────────┘            │
└─────────────────────────────────────────────────────────────────────────┘

Probes:
- Liveness:    GET /actuator/health/liveness  (initialDelay: 30s, period: 10s)
- Readiness:   GET /actuator/health/readiness (initialDelay: 10s, period: 5s)
- Startup:     GET /actuator/health           (initialDelay: 30s, period: 10s, failure: 10)

Rolling Update Strategy:
- maxSurge: 25%, maxUnavailable: 25%
```

#### 6.5.4 Full System Architecture (End-to-End)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         FULL SYSTEM ARCHITECTURE                            │
│                                                                             │
│  ┌───────────────────┐                                                      │
│  │   End Users       │                                                      │
│  │  (Browsers, Apps) │                                                      │
│  └────────┬──────────┘                                                      │
│           │                                                                 │
│  ┌────────▼──────────┐                                                      │
│  │   Load Balancer   │  (Cloud LB / Nginx Ingress)                         │
│  │   (TLS Termination│                                                      │
│  └────────┬──────────┘                                                      │
│           │                                                                 │
│  ┌────────▼─────────────────────────────────────────────────────────────┐   │
│  │                    Kubernetes Cluster (Production)                    │   │
│  │                                                                      │   │
│  │  ┌────────────┐  ┌────────────┐  ┌────────────┐                     │   │
│  │  │  Pod 1     │  │  Pod 2     │  │  Pod 3+    │  (Auto-scaled)      │   │
│  │  │employeeapp │  │employeeapp │  │employeeapp │                     │   │
│  │  │ Spring Boot│  │ Spring Boot│  │ Spring Boot│                     │   │
│  │  │  Java 17   │  │  Java 17   │  │  Java 17   │                     │   │
│  │  └─────┬──────┘  └─────┬──────┘  └─────┬──────┘                     │   │
│  │        │               │               │                             │   │
│  │        └───────────────┼───────────────┘                             │   │
│  │                        │                                             │   │
│  │              ┌─────────▼─────────┐                                   │   │
│  │              │  MySQL Service    │                                   │   │
│  │              │  (ClusterIP)      │                                   │   │
│  │              └─────────┬─────────┘                                   │   │
│  │                        │                                             │   │
│  │              ┌─────────▼─────────┐                                   │   │
│  │              │  MySQL 8.0 Pod    │                                   │   │
│  │              │  + PVC (5Gi)      │                                   │   │
│  │              └───────────────────┘                                   │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                    Local Development                                 │   │
│  │                                                                      │   │
│  │  ┌────────────┐    ┌────────────┐    ┌────────────┐                 │   │
│  │  │  Browser   │───>│ Spring Boot│───>│ H2 In-Mem  │                 │   │
│  │  │  (8080)    │    │ employeeapp│    │ Database   │                 │   │
│  │  └────────────┘    └────────────┘    └────────────┘                 │   │
│  │                                                                      │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  Database Profiles:                                                         │
│  ┌────────────────────┐    ┌────────────────────┐                          │
│  │  Local (default)   │    │  Production (prod) │                          │
│  │  H2 In-Memory      │    │  MySQL 8.0         │                          │
│  │  :mem:employeedb   │    │  Persistent PVC    │                          │
│  └────────────────────┘    └────────────────────┘                          │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 7. CI/CD Pipelines

### 7.1 CI/CD Pipeline Overview

The following diagrams and configurations illustrate a complete CI/CD pipeline for the Employee Management Application, covering code commit through production deployment.

#### 7.1.1 Pipeline Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        CI/CD PIPELINE FLOW                                  │
│                                                                             │
│  ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐          │
│  │  Commit   │────>│  Build   │────>│  Test    │────>│  Lint &  │          │
│  │  & Push   │     │  & Unit  │     │  & Integ │     │  Security│          │
│  │  (Git)    │     │  Test    │     │  Test    │     │  Scan    │          │
│  └──────────┘     └──────────┘     └──────────┘     └──────────┘          │
│                                     │              │                       │
│                              ┌──────▼──────┐  ┌────▼──────┐               │
│                              │  Gate: All  │  │ Gate: No  │               │
│                              │  Tests Pass │  │ Critical  │               │
│                              └─────────────┘  │ Vulns     │               │
│                                                └──────────┘               │
│                                                                           │
│  ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐         │
│  │  Docker  │────>│  Push    │────>│  Deploy  │────>│  Smoke   │         │
│  │  Build   │     │  Image   │     │  to Staging│   │  Tests   │         │
│  └──────────┘     └──────────┘     └──────────┘     └──────────┘         │
│                                     │              │                      │
│                              ┌──────▼──────┐  ┌───▼────────┐             │
│                              │  Gate:      │  │ Gate:       │             │
│                              │  Image Push │  │  Smoke      │             │
│                              │  Success    │  │  Tests Pass │             │
│                              └─────────────┘  └─────────────┘             │
│                                                                           │
│  ┌──────────┐     ┌──────────┐     ┌──────────┐                          │
│  │  Deploy  │────>│  Verify  │────>│  Monitor │                          │
│  │  to Prod │     │  & HPA   │     │  & Alert │                          │
│  └──────────┘     └──────────┘     └──────────┘                          │
│       │              │                                                     │
│  ┌────▼──────┐  ┌────▼──────┐                                            │
│  │ Gate:     │  │ Gate:     │                                            │
│  │ Manual    │  │ Health    │                                            │
│  │ Approval  │  │ Checks    │                                            │
│  └───────────┘  └───────────┘                                            │
└─────────────────────────────────────────────────────────────────────────────┘

Stages:
1.  Commit & Push       — Developer pushes code to Git repository
2.  Build & Unit Test   — Maven build, compile, run unit tests
3.  Test & Integration  — Run integration tests against test database
4.  Lint & Security     — Code quality checks, dependency vulnerability scan
5.  Docker Build        — Build multi-stage Docker image
6.  Push Image          — Push Docker image to registry (ECR, GCR, Docker Hub)
7.  Deploy to Staging   — Deploy to Kubernetes staging namespace
8.  Smoke Tests         — Run smoke tests against staging environment
9.  Deploy to Prod      — Deploy to Kubernetes production namespace (manual gate)
10. Verify & HPA        — Verify deployment, check HPA scaling
11. Monitor & Alert     — Monitor application health and performance
```

#### 7.1.2 Environment Promotion Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                     ENVIRONMENT PROMOTION FLOW                              │
│                                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │  DEVELOPMENT │───>│   STAGING   │───>│  PRODUCTION │───>│   MONITORING │  │
│  │              │    │             │    │             │    │             │  │
│  │  H2 In-Mem   │    │  MySQL 8.0  │    │  MySQL 8.0  │    │  Prometheus │  │
│  │  Local/Docker│    │  Docker     │    │  Kubernetes │    │  Grafana    │  │
│  │              │    │  Compose    │    │  Cluster    │    │  AlertMgr   │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│        │                    │                    │                │         │
│        │  Auto-triggered    │  Auto-triggered    │  Manual        │  Auto   │
│        │  on push           │  on staging pass   │  approval      │  always │
│        │                    │                    │                │         │
│  ──────┴────────────────────┴────────────────────┴────────────────┴────────  │
│                                                                             │
│  Quality Gates per Environment:                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                     │
│  │  Unit Tests │    │  Integration│    │  Smoke Tests│                     │
│  │  Code Lint  │    │  Tests      │    │  Performance│                     │
│  │  Security   │    │  Security   │    │  Health     │                     │
│  │  Coverage   │    │  Scan       │    │  Checks     │                     │
│  │  >= 80%     │    │  No Critical│    │  Load Test  │                     │
│  └─────────────┘    └─────────────┘    └─────────────┘                     │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 GitHub Actions Pipeline

#### 7.2.1 Complete Pipeline Configuration

```yaml
# .github/workflows/ci-cd.yml
name: Employee App CI/CD

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  REGISTRY: docker.io
  IMAGE_NAME: employeeapp
  JAVA_VERSION: '17'

jobs:
  # ─── Stage 1: Build & Test ───────────────────────────────────────
  build-and-test:
    name: Build & Test
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: maven

      - name: Build with Maven
        run: mvn -B clean compile -DskipTests

      - name: Run Unit Tests
        run: mvn -B test

      - name: Run Integration Tests
        run: mvn -B verify -Pintegration

      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-results
          path: target/surefire-reports/

      - name: Upload coverage report
        uses: codecov/codecov-action@v3
        with:
          file: ./target/site/jacoco/jacoco.xml

  # ─── Stage 2: Code Quality & Security ────────────────────────────
  quality-and-security:
    name: Quality & Security
    runs-on: ubuntu-latest
    needs: build-and-test

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: maven

      - name: Dependency vulnerability scan
        run: mvn -B dependency-check:check
        continue-on-error: true

      - name: SpotBugs analysis
        run: mvn -B spotbugs:check
        continue-on-error: true

      - name: Checkstyle
        run: mvn -B checkstyle:check

  # ─── Stage 3: Docker Build & Push ────────────────────────────────
  docker-build:
    name: Docker Build & Push
    runs-on: ubuntu-latest
    needs: quality-and-security
    if: github.ref == 'refs/heads/main'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Login to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}

      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=sha,prefix=
            type=ref,event=branch
            type=semver,pattern={{version}}

      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

  # ─── Stage 4: Deploy to Staging ──────────────────────────────────
  deploy-staging:
    name: Deploy to Staging
    runs-on: ubuntu-latest
    needs: docker-build
    environment: staging

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up kubectl
        uses: azure/setup-kubectl@v3

      - name: Configure kubeconfig
        uses: azure/k8s-set-context@v3
        with:
          method: kubeconfig
          kubeconfig: ${{ secrets.KUBE_CONFIG_STAGING }}

      - name: Deploy to staging
        run: |
          kubectl apply -f kubernetes/ -n employeeapp-staging
          kubectl set image deployment/employeeapp \
            employeeapp=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }} \
            -n employeeapp-staging

      - name: Wait for rollout
        run: |
          kubectl rollout status deployment/employeeapp \
            -n employeeapp-staging --timeout=300s

  # ─── Stage 5: Smoke Tests ────────────────────────────────────────
  smoke-tests:
    name: Smoke Tests
    runs-on: ubuntu-latest
    needs: deploy-staging
    environment: staging

    steps:
      - name: Run smoke tests
        run: |
          # Wait for readiness
          sleep 30

          # Health check
          curl -f https://staging.employeeapp.example.com/actuator/health \
            || exit 1

          # API endpoint check
          curl -f https://staging.employeeapp.example.com/api/employees \
            || exit 1

  # ─── Stage 6: Deploy to Production ───────────────────────────────
  deploy-production:
    name: Deploy to Production
    runs-on: ubuntu-latest
    needs: smoke-tests
    environment: production  # Requires manual approval
    if: github.ref == 'refs/heads/main'

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up kubectl
        uses: azure/setup-kubectl@v3

      - name: Configure kubeconfig
        uses: azure/k8s-set-context@v3
        with:
          method: kubeconfig
          kubeconfig: ${{ secrets.KUBE_CONFIG_PROD }}

      - name: Deploy to production
        run: |
          kubectl apply -f kubernetes/ -n employeeapp
          kubectl set image deployment/employeeapp \
            employeeapp=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }} \
            -n employeeapp

      - name: Wait for rollout
        run: |
          kubectl rollout status deployment/employeeapp \
            -n employeeapp --timeout=300s

      - name: Verify production health
        run: |
          curl -f https://employeeapp.example.com/actuator/health \
            || exit 1
```

#### 7.2.2 Required GitHub Secrets

| Secret Name              | Description                          | Example                          |
|--------------------------|--------------------------------------|----------------------------------|
| `DOCKER_USERNAME`        | Docker Hub username                  | `myuser`                         |
| `DOCKER_PASSWORD`        | Docker Hub password/token            | `ghp_xxxxxxxxxxxx`               |
| `KUBE_CONFIG_STAGING`    | Kubernetes config for staging        | Base64-encoded kubeconfig        |
| `KUBE_CONFIG_PROD`       | Kubernetes config for production     | Base64-encoded kubeconfig        |

#### 7.2.3 GitHub Environments

| Environment  | Protection Rules              | Purpose                          |
|--------------|-------------------------------|----------------------------------|
| `staging`    | None (auto-deploy)            | Pre-production testing           |
| `production` | Required reviewers (1+)       | Manual approval gate             |

### 7.3 Jenkins Pipeline

#### 7.3.1 Jenkinsfile (Declarative Pipeline)

```groovy
pipeline {
    agent any

    environment {
        REGISTRY      = 'docker.io'
        IMAGE_NAME    = 'employeeapp'
        JAVA_VERSION  = '17'
        DOCKER_IMAGE  = "${REGISTRY}/${IMAGE_NAME}:${BUILD_NUMBER}"
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                withEnv(["JAVA_HOME=${tool 'JDK-17'}"]) {
                    sh 'mvn -B clean compile'
                    sh 'mvn -B test'
                    sh 'mvn -B verify -Pintegration'
                }
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'Code Coverage'
                    ])
                }
            }
        }

        stage('Security Scan') {
            steps {
                sh 'mvn -B dependency-check:check'
            }
        }

        stage('Docker Build & Push') {
            when {
                branch 'main'
            }
            steps {
                script {
                    def dockerHub = credentials('docker-hub-creds')
                    withDockerRegistry(credentialsId: 'docker-hub-creds', toolName: 'docker') {
                        sh """
                            docker build -t ${DOCKER_IMAGE} .
                            docker push ${DOCKER_IMAGE}
                        """
                    }
                }
            }
        }

        stage('Deploy to Staging') {
            when {
                branch 'main'
            }
            steps {
                withKubeConfig([credentialsId: 'k8s-staging-config']) {
                    sh '''
                        kubectl apply -f kubernetes/ -n employeeapp-staging
                        kubectl set image deployment/employeeapp \
                            employeeapp=${DOCKER_IMAGE} -n employeeapp-staging
                        kubectl rollout status deployment/employeeapp \
                            -n employeeapp-staging --timeout=300s
                    '''
                }
            }
        }

        stage('Smoke Tests') {
            steps {
                sh '''
                    curl -f https://staging.employeeapp.example.com/actuator/health || exit 1
                    curl -f https://staging.employeeapp.example.com/api/employees || exit 1
                '''
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                input message: 'Deploy to production?', ok: 'Deploy'
                withKubeConfig([credentialsId: 'k8s-prod-config']) {
                    sh '''
                        kubectl apply -f kubernetes/ -n employeeapp
                        kubectl set image deployment/employeeapp \
                            employeeapp=${DOCKER_IMAGE} -n employeeapp
                        kubectl rollout status deployment/employeeapp \
                            -n employeeapp --timeout=300s
                    '''
                }
            }
        }
    }

    post {
        success {
            slackSend(channel: '#deployments', color: 'good',
                message: "Build ${env.BUILD_NUMBER} succeeded: ${env.BUILD_URL}")
        }
        failure {
            slackSend(channel: '#deployments', color: 'danger',
                message: "Build ${env.BUILD_NUMBER} failed: ${env.BUILD_URL}")
        }
    }
}
```

### 7.4 GitLab CI Pipeline

#### 7.4.1 .gitlab-ci.yml

```yaml
stages:
  - build
  - test
  - security
  - docker
  - deploy-staging
  - smoke-test
  - deploy-production

variables:
  REGISTRY: registry.gitlab.com
  IMAGE_NAME: $CI_REGISTRY_IMAGE
  JAVA_VERSION: '17'

# ─── Build Stage ─────────────────────────────────────────────
build:
  stage: build
  image: maven:3.9-eclipse-temurin-17
  script:
    - mvn -B clean compile -DskipTests
  cache:
    key: ${CI_COMMIT_REF_SLUG}
    paths:
      - ~/.m2/repository
  artifacts:
    paths:
      - target/

# ─── Test Stage ──────────────────────────────────────────────
unit-test:
  stage: test
  image: maven:3.9-eclipse-temurin-17
  script:
    - mvn -B test
  artifacts:
    reports:
      junit: target/surefire-reports/*.xml
      coverage_report:
        coverage_format: cobertura
        path: target/site/jacoco/jacoco.xml

integration-test:
  stage: test
  image: maven:3.9-eclipse-temurin-17
  script:
    - mvn -B verify -Pintegration
  services:
    - mysql:8.0
  variables:
    MYSQL_DATABASE: employeedb
    MYSQL_ROOT_PASSWORD: root

# ─── Security Stage ──────────────────────────────────────────
security-scan:
  stage: security
  image: maven:3.9-eclipse-temurin-17
  script:
    - mvn -B dependency-check:check
  allow_failure: true

# ─── Docker Stage ────────────────────────────────────────────
docker-build:
  stage: docker
  image: docker:24-dind
  services:
    - docker:24-dind
  script:
    - docker login -u $CI_REGISTRY_USER -p $CI_REGISTRY_PASSWORD $CI_REGISTRY
    - docker build -t $IMAGE_NAME:$CI_COMMIT_SHA .
    - docker push $IMAGE_NAME:$CI_COMMIT_SHA
  rules:
    - if: $CI_COMMIT_BRANCH == 'main'

# ─── Deploy Staging ──────────────────────────────────────────
deploy-staging:
  stage: deploy-staging
  image: bitnami/kubectl:latest
  script:
    - kubectl apply -f kubernetes/ -n employeeapp-staging
    - kubectl set image deployment/employeeapp
      employeeapp=$IMAGE_NAME:$CI_COMMIT_SHA -n employeeapp-staging
    - kubectl rollout status deployment/employeeapp
      -n employeeapp-staging --timeout=300s
  rules:
    - if: $CI_COMMIT_BRANCH == 'main'
  environment:
    name: staging

# ─── Smoke Tests ─────────────────────────────────────────────
smoke-test:
  stage: smoke-test
  image: curlimages/curl:latest
  script:
    - curl -f https://staging.employeeapp.example.com/actuator/health || exit 1
    - curl -f https://staging.employeeapp.example.com/api/employees || exit 1
  rules:
    - if: $CI_COMMIT_BRANCH == 'main'

# ─── Deploy Production ───────────────────────────────────────
deploy-production:
  stage: deploy-production
  image: bitnami/kubectl:latest
  script:
    - kubectl apply -f kubernetes/ -n employeeapp
    - kubectl set image deployment/employeeapp
      employeeapp=$IMAGE_NAME:$CI_COMMIT_SHA -n employeeapp
    - kubectl rollout status deployment/employeeapp
      -n employeeapp --timeout=300s
  rules:
    - if: $CI_COMMIT_BRANCH == 'main'
      when: manual
  environment:
    name: production
```

### 7.5 Pipeline Best Practices

#### 7.5.1 Security Considerations

| Practice                    | Description                                    |
|-----------------------------|------------------------------------------------|
| Secret Management           | Use CI/CD platform secrets, never hardcode credentials |
| Image Scanning              | Scan Docker images for vulnerabilities (Trivy, Snyk) |
| Dependency Scanning         | Check Maven dependencies for known CVEs        |
| Least Privilege             | Use separate kubeconfig per environment        |
| Branch Protection           | Require PR reviews before merging to main      |
| Manual Production Gate      | Require approval before production deployment  |

#### 7.5.2 Performance Optimization

| Practice                    | Description                                    |
|-----------------------------|------------------------------------------------|
| Maven Caching               | Cache ~/.m2/repository between pipeline runs   |
| Docker Layer Caching        | Use BuildKit and remote cache (GHA, GitLab)    |
| Parallel Stages             | Run tests and security scans in parallel       |
| Spot Instances              | Use spot/preemptible VMs for CI runners        |
| Pipeline Caching            | Cache build artifacts to speed up subsequent runs |

#### 7.5.3 Monitoring & Observability

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    MONITORING & OBSERVABILITY STACK                          │
│                                                                             │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐                  │
│  │  Prometheus  │───>│  Grafana     │    │  AlertManager│                  │
│  │  (Metrics)   │    │  (Dashboards)│    │  (Alerts)    │                  │
│  └──────────────┘    └──────────────┘    └──────────────┘                  │
│        ▲                       │                       │                    │
│        │                       │                       │                    │
│  ┌─────┴──────┐    ┌──────────▼──────────┐    ┌───────▼───────┐           │
│  │  employeeapp│    │  Dashboards:       │    │  Channels:     │           │
│  │  /actuator  │    │  - JVM Metrics     │    │  - Slack       │           │
│  │  /metrics   │    │  - HTTP Requests   │    │  - PagerDuty   │           │
│  └────────────┘    │  - DB Connections  │    │  - Email       │           │
│                    │  - HPA Scaling     │    └────────────────┘           │
│                    └────────────────────┘                                  │
│                                                                             │
│  Key Metrics to Monitor:                                                    │
│  - JVM: GC pauses, heap usage, thread count                                │
│  - HTTP: Request rate, error rate, response time (p50/p95/p99)             │
│  - Database: Connection pool usage, query time                              │
│  - Kubernetes: Pod restarts, HPA scaling events, resource utilization       │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 7.5.4 Rollback Strategy

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         ROLLBACK STRATEGY                                    │
│                                                                             │
│  Automatic Rollback Triggers:                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │  - Readiness probe failures > 3 consecutive                         │    │
│  │  - Error rate > 5% for 5 minutes                                   │    │
│  │  - Response time p99 > 5 seconds for 5 minutes                     │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                     │                                       │
│                    ┌────────────────▼────────────────┐                      │
│                    │  Automatic Rollback             │                      │
│                    │  kubectl rollout undo           │                      │
│                    │  deployment/employeeapp         │                      │
│                    └────────────────┬────────────────┘                      │
│                                     │                                       │
│          ┌──────────────────────────┼──────────────────────────┐            │
│          │                         │                          │            │
│  ┌───────▼────────┐      ┌────────▼────────┐      ┌──────────▼───────┐   │
│  │  Notify Team   │      │  Create Ticket  │      │  Analyze Logs    │   │
│  │  (Slack/Email) │      │  (Jira/GitHub)  │      │  (Kibana/EK)     │   │
│  └────────────────┘      └─────────────────┘      └──────────────────┘   │
│                                                                             │
│  Manual Rollback Command:                                                   │
│  kubectl rollout undo deployment/employeeapp -n employeeapp                 │
│                                                                             │
│  Kubernetes Rolling Update ensures zero-downtime deployments:               │
│  - maxSurge: 25%  (extra pods during update)                               │
│  - maxUnavailable: 25%  (pods that can be down during update)              │
└─────────────────────────────────────────────────────────────────────────────┘
```
