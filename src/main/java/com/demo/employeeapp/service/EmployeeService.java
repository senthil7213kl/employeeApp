package com.demo.employeeapp.service;

import com.demo.employeeapp.dto.DepartmentResponseDTO;
import com.demo.employeeapp.dto.EmployeeRequestDTO;
import com.demo.employeeapp.dto.EmployeeResponseDTO;
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

    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
        return toResponseDTO(employee);
    }

    public EmployeeResponseDTO getEmployeeByEmail(String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with email: " + email));
        return toResponseDTO(employee);
    }

    public List<EmployeeResponseDTO> getEmployeesByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Employee with email already exists: " + dto.getEmail());
        }
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + dto.getDepartmentId()));

        Employee employee = Employee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .position(dto.getPosition())
                .salary(dto.getSalary())
                .department(department)
                .build();

        return toResponseDTO(employeeRepository.save(employee));
    }

    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee employee = getEmployeeByIdEntity(id);
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setPosition(dto.getPosition());
        employee.setSalary(dto.getSalary());
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            employee.setDepartment(department);
        }
        return toResponseDTO(employeeRepository.save(employee));
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

    private Employee getEmployeeByIdEntity(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
    }

    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        DepartmentResponseDTO departmentDTO = null;
        if (employee.getDepartment() != null) {
            Department department = employee.getDepartment();
            departmentDTO = DepartmentResponseDTO.builder()
                    .id(department.getId())
                    .departmentName(department.getDepartmentName())
                    .departmentLocation(department.getDepartmentLocation())
                    .departmentDescription(department.getDepartmentDescription())
                    .build();
        }

        return EmployeeResponseDTO.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .position(employee.getPosition())
                .salary(employee.getSalary())
                .department(departmentDTO)
                .build();
    }
}
