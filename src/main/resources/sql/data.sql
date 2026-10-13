-- =====================================================
-- Sample data for Employee Management System
-- =====================================================

-- Insert departments
INSERT INTO departments (id, department_name, department_location, department_description) VALUES
(1, 'Engineering', 'Building A', 'Software Development and Engineering'),
(2, 'Marketing', 'Building B', 'Marketing and Brand Management'),
(3, 'Human Resources', 'Building C', 'Human Resources and Recruitment'),
(4, 'Finance', 'Building D', 'Finance and Accounting'),
(5, 'Sales', 'Building E', 'Sales and Business Development');

-- Reset the AUTO_INCREMENT counter for departments to avoid primary key conflicts
ALTER TABLE departments ALTER COLUMN id RESTART WITH 6;

-- Insert employees
INSERT INTO employees (id, first_name, last_name, email, phone, position, salary, department_id) VALUES
-- Engineering department
(1, 'John', 'Doe', 'john.doe@example.com', '123-456-7890', 'Senior Software Engineer', 95000.00, 1),
(2, 'Jane', 'Smith', 'jane.smith@example.com', '234-567-8901', 'Software Engineer', 85000.00, 1),
(3, 'Bob', 'Johnson', 'bob.johnson@example.com', '345-678-9012', 'DevOps Engineer', 90000.00, 1),
(4, 'Alice', 'Williams', 'alice.williams@example.com', '456-789-0123', 'Engineering Manager', 120000.00, 1),
-- Marketing department
(5, 'Charlie', 'Brown', 'charlie.brown@example.com', '567-890-1234', 'Marketing Manager', 100000.00, 2),
(6, 'Diana', 'Davis', 'diana.davis@example.com', '678-901-2345', 'Content Strategist', 75000.00, 2),
(7, 'Edward', 'Miller', 'edward.miller@example.com', '789-012-3456', 'SEO Specialist', 70000.00, 2),
-- Human Resources department
(8, 'Fiona', 'Wilson', 'fiona.wilson@example.com', '890-123-4567', 'HR Director', 110000.00, 3),
(9, 'George', 'Taylor', 'george.taylor@example.com', '901-234-5678', 'Recruiter', 65000.00, 3),
-- Finance department
(10, 'Hannah', 'Anderson', 'hannah.anderson@example.com', '012-345-6789', 'Finance Manager', 105000.00, 4),
(11, 'Ivan', 'Thomas', 'ivan.thomas@example.com', '111-222-3333', 'Accountant', 72000.00, 4),
(12, 'Julia', 'Jackson', 'julia.jackson@example.com', '222-333-4444', 'Financial Analyst', 78000.00, 4),
-- Sales department
(13, 'Kevin', 'White', 'kevin.white@example.com', '333-444-5555', 'Sales Director', 115000.00, 5),
(14, 'Laura', 'Harris', 'laura.harris@example.com', '444-555-6666', 'Sales Representative', 68000.00, 5),
(15, 'Michael', 'Martin', 'michael.martin@example.com', '555-666-7777', 'Account Executive', 82000.00, 5);

-- Reset the AUTO_INCREMENT counter for employees to avoid primary key conflicts
ALTER TABLE employees ALTER COLUMN id RESTART WITH 16;
