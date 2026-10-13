# EmployeeApp API Documentation

## Base URL
```
http://localhost:8080/api
```

---

## Table of Contents
1. [Employee APIs](#1-employee-apis)
2. [Department APIs](#2-department-apis)

---

## 1. Employee APIs

Base path: `/api/employees`

---

### 1.1 Create Employee (POST)

**Endpoint:** `POST /api/employees`

**Description:** Creates a new employee.

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "position": "Software Engineer",
  "salary": 95000.00,
  "departmentId": 1
}
```

**Validation Rules:**
- `firstName`: Required, must not be blank
- `lastName`: Required, must not be blank
- `email`: Required, must be a valid email format
- `phone`: Optional
- `position`: Optional
- `salary`: Optional
- `departmentId`: Required, must not be null

**Success Response (201 Created):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "position": "Software Engineer",
  "salary": 95000.00,
  "department": {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development Department"
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-10-13T14:35:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": [
    {
      "field": "firstName",
      "message": "First name is required"
    },
    {
      "field": "email",
      "message": "Email should be valid"
    }
  ]
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "position": "Software Engineer",
    "salary": 95000.00,
    "departmentId": 1
  }'
```

---

### 1.2 Get All Employees (GET)

**Endpoint:** `GET /api/employees`

**Description:** Returns a list of all employees.

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "position": "Software Engineer",
    "salary": 95000.00,
    "department": {
      "id": 1,
      "departmentName": "Engineering",
      "departmentLocation": "Building A",
      "departmentDescription": "Software Development Department"
    }
  },
  {
    "id": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com",
    "phone": "+1-555-0124",
    "position": "Product Manager",
    "salary": 110000.00,
    "department": {
      "id": 2,
      "departmentName": "Product",
      "departmentLocation": "Building B",
      "departmentDescription": "Product Management Department"
    }
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/employees
```

---

### 1.3 Get Employee by ID (GET)

**Endpoint:** `GET /api/employees/{id}`

**Description:** Returns an employee by their ID.

**Path Parameters:**
| Parameter | Type   | Description      |
|-----------|--------|------------------|
| `id`      | Long   | Employee ID      |

**Success Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "position": "Software Engineer",
  "salary": 95000.00,
  "department": {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development Department"
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-10-13T14:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee with id 999 not found"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/employees/1
```

---

### 1.4 Get Employee by Email (GET)

**Endpoint:** `GET /api/employees/search/by-email`

**Description:** Returns an employee by their email address.

**Query Parameters:**
| Parameter | Type   | Description      |
|-----------|--------|------------------|
| `email`   | String | Employee email   |

**Success Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "position": "Software Engineer",
  "salary": 95000.00,
  "department": {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development Department"
  }
}
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/employees/search/by-email?email=john.doe@example.com"
```

---

### 1.5 Get Employees by Department (GET)

**Endpoint:** `GET /api/employees/department/{departmentId}`

**Description:** Returns all employees in a specific department.

**Path Parameters:**
| Parameter      | Type   | Description        |
|----------------|--------|--------------------|
| `departmentId` | Long   | Department ID      |

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "position": "Software Engineer",
    "salary": 95000.00,
    "department": {
      "id": 1,
      "departmentName": "Engineering",
      "departmentLocation": "Building A",
      "departmentDescription": "Software Development Department"
    }
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/employees/department/1
```

---

### 1.6 Update Employee (PUT)

**Endpoint:** `PUT /api/employees/{id}`

**Description:** Updates an existing employee by ID.

**Path Parameters:**
| Parameter | Type   | Description      |
|-----------|--------|------------------|
| `id`      | Long   | Employee ID      |

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe.updated@example.com",
  "phone": "+1-555-9999",
  "position": "Senior Software Engineer",
  "salary": 120000.00,
  "departmentId": 1
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe.updated@example.com",
  "phone": "+1-555-9999",
  "position": "Senior Software Engineer",
  "salary": 120000.00,
  "department": {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development Department"
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-10-13T14:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee with id 999 not found"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe.updated@example.com",
    "phone": "+1-555-9999",
    "position": "Senior Software Engineer",
    "salary": 120000.00,
    "departmentId": 1
  }'
```

---

### 1.7 Delete Employee (DELETE)

**Endpoint:** `DELETE /api/employees/{id}`

**Description:** Deletes an employee by ID.

**Path Parameters:**
| Parameter | Type   | Description      |
|-----------|--------|------------------|
| `id`      | Long   | Employee ID      |

**Success Response (204 No Content):**
```
(Empty response body)
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-10-13T14:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Employee with id 999 not found"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/employees/1
```

---

### 1.8 Search Employees (GET)

**Endpoint:** `GET /api/employees/search`

**Description:** Searches employees by keyword with pagination and fetches data from an external API.

**Query Parameters:**
| Parameter       | Type   | Default                  | Description                              |
|-----------------|--------|--------------------------|------------------------------------------|
| `keyword`       | String | (required)               | Search keyword for firstName, lastName, or email |
| `page`          | int    | `0`                      | Page number (0-based)                    |
| `size`          | int    | `10`                     | Page size                                |
| `sortBy`        | String | `firstName`              | Sort field                               |
| `direction`     | String | `ASC`                    | Sort direction: ASC or DESC              |
| `externalApiUrl`| String | `https://www.google.com` | External API URL to call                 |

**Success Response (200 OK):**
```json
{
  "employees": {
    "content": [
      {
        "id": 1,
        "firstName": "John",
        "lastName": "Doe",
        "email": "john.doe@example.com",
        "phone": "+1-555-0123",
        "position": "Software Engineer",
        "salary": 95000.00,
        "department": {
          "id": 1,
          "departmentName": "Engineering",
          "departmentLocation": "Building A",
          "departmentDescription": "Software Development Department"
        }
      }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
  },
  "externalApiData": {
    "status": "success"
  },
  "externalApiUrl": "https://www.google.com",
  "externalApiSuccess": true
}
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/employees/search?keyword=John&page=0&size=10&sortBy=firstName&direction=ASC&externalApiUrl=https://www.google.com"
```

---

## 2. Department APIs

Base path: `/api/departments`

---

### 2.1 Create Department (POST)

**Endpoint:** `POST /api/departments`

**Description:** Creates a new department.

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "departmentName": "Engineering",
  "departmentLocation": "Building A",
  "departmentDescription": "Software Development Department"
}
```

**Validation Rules:**
- `departmentName`: Required, must not be blank
- `departmentLocation`: Optional
- `departmentDescription`: Optional

**Success Response (201 Created):**
```json
{
  "id": 1,
  "departmentName": "Engineering",
  "departmentLocation": "Building A",
  "departmentDescription": "Software Development Department"
}
```

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2026-10-13T14:35:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": [
    {
      "field": "departmentName",
      "message": "Department name is required"
    }
  ]
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/departments \
  -H "Content-Type: application/json" \
  -d '{
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development Department"
  }'
```

---

### 2.2 Get All Departments (GET)

**Endpoint:** `GET /api/departments`

**Description:** Returns a list of all departments.

**Success Response (200 OK):**
```json
[
  {
    "id": 1,
    "departmentName": "Engineering",
    "departmentLocation": "Building A",
    "departmentDescription": "Software Development Department"
  },
  {
    "id": 2,
    "departmentName": "Product",
    "departmentLocation": "Building B",
    "departmentDescription": "Product Management Department"
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/departments
```

---

### 2.3 Get Department by ID (GET)

**Endpoint:** `GET /api/departments/{id}`

**Description:** Returns a department by its ID.

**Path Parameters:**
| Parameter | Type   | Description        |
|-----------|--------|--------------------|
| `id`      | Long   | Department ID      |

**Success Response (200 OK):**
```json
{
  "id": 1,
  "departmentName": "Engineering",
  "departmentLocation": "Building A",
  "departmentDescription": "Software Development Department"
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-10-13T14:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Department with id 999 not found"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8080/api/departments/1
```

---

### 2.4 Get Department by Name (GET)

**Endpoint:** `GET /api/departments/search/by-name`

**Description:** Returns a department by its name.

**Query Parameters:**
| Parameter        | Type     | Description         |
|------------------|----------|---------------------|
| `departmentName` | String[] | Department name     |

**Success Response (200 OK):**
```json
{
  "id": 1,
  "departmentName": "Engineering",
  "departmentLocation": "Building A",
  "departmentDescription": "Software Development Department"
}
```

**cURL Example:**
```bash
curl -X GET "http://localhost:8080/api/departments/search/by-name?departmentName=Engineering"
```

---

### 2.5 Update Department (PUT)

**Endpoint:** `PUT /api/departments/{id}`

**Description:** Updates an existing department by ID.

**Path Parameters:**
| Parameter | Type   | Description        |
|-----------|--------|--------------------|
| `id`      | Long   | Department ID      |

**Request Body:**
```json
{
  "departmentName": "Engineering",
  "departmentLocation": "Building C",
  "departmentDescription": "Updated Software Development Department"
}
```

**Success Response (200 OK):**
```json
{
  "id": 1,
  "departmentName": "Engineering",
  "departmentLocation": "Building C",
  "departmentDescription": "Updated Software Development Department"
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-10-13T14:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Department with id 999 not found"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/departments/1 \
  -H "Content-Type: application/json" \
  -d '{
    "departmentName": "Engineering",
    "departmentLocation": "Building C",
    "departmentDescription": "Updated Software Development Department"
  }'
```

---

### 2.6 Delete Department (DELETE)

**Endpoint:** `DELETE /api/departments/{id}`

**Description:** Deletes a department by ID.

**Path Parameters:**
| Parameter | Type   | Description        |
|-----------|--------|--------------------|
| `id`      | Long   | Department ID      |

**Success Response (204 No Content):**
```
(Empty response body)
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2026-10-13T14:35:00",
  "status": 404,
  "error": "Not Found",
  "message": "Department with id 999 not found"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/departments/1
```

---

## Summary of All Endpoints

| Method   | Endpoint                                      | Description                              |
|----------|-----------------------------------------------|------------------------------------------|
| `POST`   | `/api/employees`                              | Create a new employee                    |
| `GET`    | `/api/employees`                              | Get all employees                        |
| `GET`    | `/api/employees/{id}`                         | Get employee by ID                       |
| `GET`    | `/api/employees/search/by-email`              | Get employee by email                    |
| `GET`    | `/api/employees/department/{departmentId}`    | Get employees by department              |
| `PUT`    | `/api/employees/{id}`                         | Update an employee                       |
| `DELETE` | `/api/employees/{id}`                         | Delete an employee                       |
| `GET`    | `/api/employees/search`                       | Search employees with pagination         |
| `POST`   | `/api/departments`                            | Create a new department                  |
| `GET`    | `/api/departments`                            | Get all departments                      |
| `GET`    | `/api/departments/{id}`                       | Get department by ID                     |
| `GET`    | `/api/departments/search/by-name`             | Get department by name                   |
| `PUT`    | `/api/departments/{id}`                       | Update a department                      |
| `DELETE` | `/api/departments/{id}`                       | Delete a department                      |
