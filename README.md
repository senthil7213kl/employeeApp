# Employee Management API

A Spring Boot REST API for managing employees and departments, built with Spring Data JPA, H2 in-memory database, and Swagger UI for API documentation.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Swagger UI](#swagger-ui)
- [H2 Console](#h2-console)
- [Docker](#docker)
- [Docker Compose](#docker-compose)
- [Kubernetes Deployment](#kubernetes-deployment)
- [API Examples](#api-examples)

## ✨ Features

- **Department Management**: Full CRUD operations for departments
- **Employee Management**: Full CRUD operations for employees
- **Department-Employee Relationship**: Many-to-One relationship (one department has many employees)
- **Search Functionality**: Search employees by email, departments by name
- **Paginated Search**: Search employees with pagination, sorting, and external API integration
- **Filter by Department**: Get all employees in a specific department
- **Swagger UI**: Interactive API documentation
- **H2 Console**: In-memory database browser
- **Docker Support**: Multi-stage Docker build for easy deployment
- **Docker Compose**: Local development with MySQL database
- **Kubernetes Deployment**: Production-ready Kubernetes manifests with HPA, Ingress, and auto-scaling
- **Global Exception Handling**: Consistent error responses

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot 4.0.8**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Lombok** (boilerplate reduction)
- **SpringDoc OpenAPI 3** (Swagger UI)
- **Maven**
- **Docker & Docker Compose**
- **Kubernetes**

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (optional, for containerized deployment)
- Docker Compose (optional, for local development with MySQL)
- Kubernetes cluster (optional, for production deployment)

## 📁 Project Structure

```
employeeapp/
├── src/main/java/com/demo/employeeapp/
│   ├── config/
│   │   └── OpenAPIConfig.java
│   ├── controller/
│   │   ├── DepartmentController.java
│   │   └── EmployeeController.java
│   ├── entity/
│   │   ├── Department.java
│   │   └── Employee.java
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   ├── repository/
│   │   ├── DepartmentRepository.java
│   │   └── EmployeeRepository.java
│   ├── service/
│   │   ├── DepartmentService.java
│   │   └── EmployeeService.java
│   └── EmployeeappApplication.java
├── src/main/resources/
│   ├── application.properties
│   ├── application-prod.properties
│   └── sql/
├── kubernetes/
│   ├── namespace.yaml
│   ├── secret.yaml
│   ├── configmap.yaml
│   ├── mysql-pvc.yaml
│   ├── mysql-deployment.yaml
│   ├── mysql-service.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── ingress.yaml
│   ├── hpa.yaml
│   └── README.md
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd employeeapp
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 🔌 API Endpoints

### Department Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/departments` | Get all departments |
| GET | `/api/departments/{id}` | Get department by ID |
| GET | `/api/departments/search/by-name?departmentName={name}` | Get department by name |
| POST | `/api/departments` | Create a new department |
| PUT | `/api/departments/{id}` | Update a department |
| DELETE | `/api/departments/{id}` | Delete a department |

### Employee Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/employees` | Get all employees |
| GET | `/api/employees/{id}` | Get employee by ID |
| GET | `/api/employees/search/by-email?email={email}` | Get employee by email |
| GET | `/api/employees/department/{departmentId}` | Get employees by department |
| GET | `/api/employees/search?keyword={keyword}` | Search employees with pagination and external API |
| POST | `/api/employees` | Create a new employee |
| PUT | `/api/employees/{id}` | Update an employee |
| DELETE | `/api/employees/{id}` | Delete an employee |

## 📖 Swagger UI

Interactive API documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON spec:

```
http://localhost:8080/api-docs
```

**Note:** Swagger UI provides an interactive interface to explore and test all API endpoints without using curl or Postman.

## 🗄 H2 Console

The H2 database console is available at:

```
http://localhost:8080/h2-console
```

**Connection Details:**
- **JDBC URL**: `jdbc:h2:mem:employeedb`
- **Username**: `sa`
- **Password**: (leave blank)

## 🐳 Docker

### Build the Docker Image

```bash
docker build -t employeeapp:latest .
```

### Run the Container (with H2 in-memory database)

```bash
docker run -d -p 8080:8080 --name employee-app employeeapp:latest
```

### Access the Application

- **API**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console`

### Stop the Container

```bash
docker stop employee-app
docker rm employee-app
```

## 🐙 Docker Compose

Docker Compose provides a complete local development environment with MySQL database and phpMyAdmin.

### Start the Environment

```bash
docker-compose up -d
```

This starts three services:
- **employeeapp**: The Spring Boot application (port 8080)
- **mysql**: MySQL 8.0 database (port 3306)
- **phpmyadmin**: Database management UI (port 8081)

### Access the Services

- **API**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **phpMyAdmin**: `http://localhost:8081`

### View Logs

```bash
docker-compose logs -f
```

### Stop the Environment

```bash
docker-compose down
```

### Stop and Remove Data

```bash
docker-compose down -v
```

## ☸️ Kubernetes Deployment

The application includes production-ready Kubernetes manifests for deploying to any Kubernetes cluster.

### Quick Start

```bash
# Build the Docker image
docker build -t employeeapp:latest .

# Deploy all resources
kubectl apply -f kubernetes/

# Verify deployment
kubectl get pods -n employeeapp
```

### Access the Application

```bash
# Port forward for local access
kubectl port-forward svc/employeeapp 8080:80 -n employeeapp

# Access the API
curl http://localhost:8080/api/employees
```

### What's Included

- **Namespace**: Isolated `employeeapp` namespace
- **MySQL Database**: Persistent storage with PVC
- **Application Deployment**: 3 replicas with rolling updates
- **Services**: ClusterIP services for internal communication
- **Ingress**: NGINX Ingress with TLS support
- **HPA**: Auto-scaling from 2 to 10 replicas based on CPU/memory
- **Health Probes**: Liveness, readiness, and startup probes
- **Resource Limits**: CPU and memory requests/limits

### Production Features

- Rolling update strategy (zero downtime deployments)
- Horizontal Pod Autoscaler (CPU: 70%, Memory: 80%)
- Health checks with Spring Boot Actuator
- Persistent MySQL storage
- TLS/SSL via Ingress
- ConfigMap and Secret management

For detailed deployment instructions, see [kubernetes/README.md](kubernetes/README.md).

## 📝 API Examples

### Create a Department

```bash
curl -X POST http://localhost:8080/api/departments \
  -H "Content-Type: application/json" \
  -d '{
    "departmentName": "Engineering",
    "departmentLocation": "New York",
    "departmentDescription": "Software Development Department"
  }'
```

### Create an Employee

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "123-456-7890",
    "position": "Software Engineer",
    "salary": 95000.00,
    "department": {
      "id": 1
    }
  }'
```

### Get All Employees

```bash
curl http://localhost:8080/api/employees
```

### Get Employees by Department

```bash
curl http://localhost:8080/api/employees/department/1
```

### Search Employees with Pagination

```bash
curl "http://localhost:8080/api/employees/search?keyword=John&page=0&size=5&sortBy=firstName&direction=ASC&externalApiUrl=https://www.google.com"
```

### Update an Employee

```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "987-654-3210",
    "position": "Senior Software Engineer",
    "salary": 110000.00,
    "department": {
      "id": 1
    }
  }'
```

### Delete an Employee

```bash
curl -X DELETE http://localhost:8080/api/employees/1
```

## 📄 License

This project is licensed under the Apache 2.0 License.
