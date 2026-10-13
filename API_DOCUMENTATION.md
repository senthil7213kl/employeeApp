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
