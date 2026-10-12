package com.demo.employeeapp.service;

import com.demo.employeeapp.dto.SearchResponse;
import com.demo.employeeapp.entity.Department;
import com.demo.employeeapp.entity.Employee;
import com.demo.employeeapp.repository.DepartmentRepository;
import com.demo.employeeapp.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RestTemplate restTemplate;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
    }

    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));
    }

    public List<Employee> getEmployeesByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Employee with email already exists: " + employee.getEmail());
        }
        Department department = departmentRepository.findById(employee.getDepartment().getId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + employee.getDepartment().getId()));
        employee.setDepartment(department);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = getEmployeeById(id);
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhone(employeeDetails.getPhone());
        employee.setPosition(employeeDetails.getPosition());
        employee.setSalary(employeeDetails.getSalary());
        if (employeeDetails.getDepartment() != null) {
            Department department = departmentRepository.findById(employeeDetails.getDepartment().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + employeeDetails.getDepartment().getId()));
            employee.setDepartment(department);
        }
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    public SearchResponse searchEmployees(String keyword, Pageable pageable, String externalApiUrl) {
        Page<Employee> employees = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword, keyword, keyword, pageable);

        Map<String, Object> externalApiData = new HashMap<>();
        boolean externalApiSuccess = false;

        try {
            log.info("Calling external API: {}", externalApiUrl);
            String response = restTemplate.getForObject(externalApiUrl, String.class);

            if (response != null) {
                externalApiData.put("status", "success");
                externalApiData.put("contentLength", response.length());
                externalApiData.put("preview", response.length() > 200 ? response.substring(0, 200) + "..." : response);
                externalApiSuccess = true;
                log.info("External API call successful");
            }
        } catch (Exception e) {
            log.error("Failed to call external API: {}", externalApiUrl, e);
            externalApiData.put("status", "error");
            externalApiData.put("message", e.getMessage());
        }

        return SearchResponse.builder()
                .employees(employees)
                .externalApiData(externalApiData)
                .externalApiUrl(externalApiUrl)
                .externalApiSuccess(externalApiSuccess)
                .build();
    }
}
