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
- **Global Exception Handling**: Consistent error responses

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot 4.0.8**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Lombok** (boilerplate reduction)
- **SpringDoc OpenAPI 3** (Swagger UI)
- **Maven**

## 📦 Prerequisites

- Java 17 or higher
- Maven 3.6+

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
