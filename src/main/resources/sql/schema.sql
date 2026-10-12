-- =====================================================
-- Schema creation script for Employee Management System
-- =====================================================

-- Create departments table first (referenced by employees)
CREATE TABLE IF NOT EXISTS departments (
    id                  BIGINT          PRIMARY KEY AUTO_INCREMENT,
    department_name     VARCHAR(100)    NOT NULL,
    department_location VARCHAR(200),
    department_description VARCHAR(500)
);

-- Create employees table with foreign key to departments
CREATE TABLE IF NOT EXISTS employees (
    id          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    first_name  VARCHAR(50)   NOT NULL,
    last_name   VARCHAR(50)   NOT NULL,
    email       VARCHAR(100)  NOT NULL UNIQUE,
    phone       VARCHAR(20),
    position    VARCHAR(100),
    salary      DOUBLE,
    department_id BIGINT      NOT NULL,
    CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id)
);

-- Index for faster lookups
CREATE INDEX IF NOT EXISTS idx_employees_email ON employees(email);
CREATE INDEX IF NOT EXISTS idx_employees_department ON employees(department_id);
