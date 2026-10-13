package com.demo.employeeapp.service;

import com.demo.employeeapp.dto.EmployeeDTO;
import com.demo.employeeapp.dto.SearchResponse;
import com.demo.employeeapp.entity.Department;
import com.demo.employeeapp.entity.Employee;
import com.demo.employeeapp.repository.DepartmentRepository;
import com.demo.employeeapp.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceUnitTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee testEmployee;
    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L)
                .departmentName("Engineering")
                .departmentLocation("Building A")
                .departmentDescription("Software Development")
                .build();

        testEmployee = Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .position("Software Engineer")
                .salary(80000.0)
                .department(testDepartment)
                .build();
    }

    @Nested
    @DisplayName("Get All Employees")
    class GetAllEmployees {

        @Test
        @DisplayName("Should return all employees")
        void shouldReturnAllEmployees() {
            Employee employee2 = Employee.builder()
                    .id(2L)
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("jane.smith@example.com")
                    .department(testDepartment)
                    .build();

            when(employeeRepository.findAll()).thenReturn(Arrays.asList(testEmployee, employee2));

            List<Employee> result = employeeService.getAllEmployees();

            assertEquals(2, result.size());
            verify(employeeRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no employees exist")
        void shouldReturnEmptyListWhenNoEmployees() {
            when(employeeRepository.findAll()).thenReturn(List.of());

            List<Employee> result = employeeService.getAllEmployees();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Employee By ID")
    class GetEmployeeById {

        @Test
        @DisplayName("Should return employee when found")
        void shouldReturnEmployeeWhenFound() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

            Employee result = employeeService.getEmployeeById(1L);

            assertEquals("John", result.getFirstName());
            assertEquals("john.doe@example.com", result.getEmail());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when employee not found")
        void shouldThrowWhenEmployeeNotFound() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> employeeService.getEmployeeById(999L));
        }
    }

    @Nested
    @DisplayName("Get Employee By Email")
    class GetEmployeeByEmail {

        @Test
        @DisplayName("Should return employee when found by email")
        void shouldReturnEmployeeWhenFoundByEmail() {
            when(employeeRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testEmployee));

            Employee result = employeeService.getEmployeeByEmail("john.doe@example.com");

            assertEquals("John", result.getFirstName());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when employee not found by email")
        void shouldThrowWhenEmployeeNotFoundByEmail() {
            when(employeeRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    employeeService.getEmployeeByEmail("nonexistent@example.com"));
        }
    }

    @Nested
    @DisplayName("Get Employees By Department ID")
    class GetEmployeesByDepartmentId {

        @Test
        @DisplayName("Should return employees for given department")
        void shouldReturnEmployeesByDepartmentId() {
            Employee employee2 = Employee.builder()
                    .id(2L)
                    .firstName("Jane")
                    .lastName("Smith")
                    .department(testDepartment)
                    .build();

            when(employeeRepository.findByDepartmentId(1L))
                    .thenReturn(Arrays.asList(testEmployee, employee2));

            List<Employee> result = employeeService.getEmployeesByDepartmentId(1L);

            assertEquals(2, result.size());
            verify(employeeRepository).findByDepartmentId(1L);
        }

        @Test
        @DisplayName("Should return empty list when no employees in department")
        void shouldReturnEmptyListWhenNoEmployeesInDepartment() {
            when(employeeRepository.findByDepartmentId(1L)).thenReturn(List.of());

            List<Employee> result = employeeService.getEmployeesByDepartmentId(1L);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Create Employee")
    class CreateEmployee {

        @Test
        @DisplayName("Should create employee successfully")
        void shouldCreateEmployeeSuccessfully() {
            EmployeeDTO dto = EmployeeDTO.builder()
                    .firstName("New")
                    .lastName("User")
                    .email("new.user@example.com")
                    .departmentId(1L)
                    .build();

            when(employeeRepository.existsByEmail("new.user@example.com")).thenReturn(false);
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
                Employee saved = invocation.getArgument(0);
                return saved;
            });

            Employee result = employeeService.createEmployee(dto);

            assertEquals("New", result.getFirstName());
            assertEquals("User", result.getLastName());
            assertEquals("new.user@example.com", result.getEmail());
            verify(employeeRepository).save(any(Employee.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when email already exists")
        void shouldThrowWhenEmailAlreadyExists() {
            EmployeeDTO dto = EmployeeDTO.builder()
                    .email("john.doe@example.com")
                    .departmentId(1L)
                    .build();

            when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () ->
                    employeeService.createEmployee(dto));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when department not found")
        void shouldThrowWhenDepartmentNotFound() {
            EmployeeDTO dto = EmployeeDTO.builder()
                    .email("new.user@example.com")
                    .departmentId(999L)
                    .build();

            when(employeeRepository.existsByEmail("new.user@example.com")).thenReturn(false);
            when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    employeeService.createEmployee(dto));
        }
    }

    @Nested
    @DisplayName("Update Employee")
    class UpdateEmployee {

        @Test
        @DisplayName("Should update employee successfully")
        void shouldUpdateEmployeeSuccessfully() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

            Employee updated = Employee.builder()
                    .firstName("Updated")
                    .lastName("Name")
                    .email("updated@example.com")
                    .phone("999-999-9999")
                    .position("Senior Engineer")
                    .salary(90000.0)
                    .build();

            Employee result = employeeService.updateEmployee(1L, updated);

            assertEquals("Updated", result.getFirstName());
            verify(employeeRepository).save(testEmployee);
        }

        @Test
        @DisplayName("Should update employee department when provided")
        void shouldUpdateDepartmentWhenProvided() {
            Department newDepartment = Department.builder()
                    .id(2L)
                    .departmentName("Marketing")
                    .build();

            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(departmentRepository.findById(2L)).thenReturn(Optional.of(newDepartment));
            when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

            Employee updated = Employee.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .department(newDepartment)
                    .build();

            employeeService.updateEmployee(1L, updated);

            verify(departmentRepository).findById(2L);
            verify(employeeRepository).save(testEmployee);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when employee not found")
        void shouldThrowWhenEmployeeNotFound() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    employeeService.updateEmployee(999L, testEmployee));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when new department not found")
        void shouldThrowWhenNewDepartmentNotFound() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

            Employee updated = Employee.builder()
                    .department(Department.builder().id(999L).build())
                    .build();

            assertThrows(EntityNotFoundException.class, () ->
                    employeeService.updateEmployee(1L, updated));
        }
    }

    @Nested
    @DisplayName("Delete Employee")
    class DeleteEmployee {

        @Test
        @DisplayName("Should delete employee successfully")
        void shouldDeleteEmployeeSuccessfully() {
            when(employeeRepository.existsById(1L)).thenReturn(true);
            doNothing().when(employeeRepository).deleteById(1L);

            assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));
            verify(employeeRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when employee not found")
        void shouldThrowWhenEmployeeNotFound() {
            when(employeeRepository.existsById(999L)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () ->
                    employeeService.deleteEmployee(999L));
        }
    }

    @Nested
    @DisplayName("Search Employees")
    class SearchEmployees {

        @Test
        @DisplayName("Should search employees and call external API successfully")
        void shouldSearchEmployeesWithSuccessfulExternalApi() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Employee> employeePage = new PageImpl<>(List.of(testEmployee), pageable, 1);
            String externalApiResponse = "Hello from external API";

            when(employeeRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            "John", "John", "John", pageable))
                    .thenReturn(employeePage);
            when(restTemplate.getForObject("https://www.google.com", String.class))
                    .thenReturn(externalApiResponse);

            SearchResponse response = employeeService.searchEmployees("John", pageable, "https://www.google.com");

            assertNotNull(response);
            assertEquals(1, response.getEmployees().getTotalElements());
            assertEquals("John", response.getEmployees().getContent().get(0).getFirstName());
            assertTrue(response.isExternalApiSuccess());
            assertEquals("success", response.getExternalApiData().get("status"));
            assertEquals(externalApiResponse.length(), response.getExternalApiData().get("contentLength"));
            assertEquals(externalApiResponse, response.getExternalApiData().get("preview"));
            assertEquals("https://www.google.com", response.getExternalApiUrl());
        }

        @Test
        @DisplayName("Should handle external API failure gracefully")
        void shouldHandleExternalApiFailure() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Employee> employeePage = new PageImpl<>(List.of(testEmployee), pageable, 1);

            when(employeeRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            "John", "John", "John", pageable))
                    .thenReturn(employeePage);
            when(restTemplate.getForObject("https://invalid.url", String.class))
                    .thenThrow(new RuntimeException("Connection refused"));

            SearchResponse response = employeeService.searchEmployees("John", pageable, "https://invalid.url");

            assertNotNull(response);
            assertEquals(1, response.getEmployees().getTotalElements());
            assertFalse(response.isExternalApiSuccess());
            assertEquals("error", response.getExternalApiData().get("status"));
            assertNotNull(response.getExternalApiData().get("message"));
        }

        @Test
        @DisplayName("Should return empty results when no employees match")
        void shouldReturnEmptyResultsWhenNoMatch() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Employee> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            String externalApiResponse = "OK";

            when(employeeRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            "Nonexistent", "Nonexistent", "Nonexistent", pageable))
                    .thenReturn(emptyPage);
            when(restTemplate.getForObject("https://www.google.com", String.class))
                    .thenReturn(externalApiResponse);

            SearchResponse response = employeeService.searchEmployees("Nonexistent", pageable, "https://www.google.com");

            assertNotNull(response);
            assertTrue(response.getEmployees().isEmpty());
            assertEquals(0, response.getEmployees().getTotalElements());
            assertTrue(response.isExternalApiSuccess());
        }

        @Test
        @DisplayName("Should truncate preview when response is longer than 200 characters")
        void shouldTruncateLongPreview() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Employee> employeePage = new PageImpl<>(List.of(), pageable, 0);
            String longResponse = "x".repeat(300);

            when(employeeRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            "", "", "", pageable))
                    .thenReturn(employeePage);
            when(restTemplate.getForObject("https://www.google.com", String.class))
                    .thenReturn(longResponse);

            SearchResponse response = employeeService.searchEmployees("", pageable, "https://www.google.com");

            assertTrue(response.isExternalApiSuccess());
            String preview = (String) response.getExternalApiData().get("preview");
            assertEquals(203, preview.length()); // 200 chars + "..."
            assertTrue(preview.endsWith("..."));
        }

        @Test
        @DisplayName("Should return full response as preview when shorter than 200 characters")
        void shouldReturnFullPreviewForShortResponse() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Employee> employeePage = new PageImpl<>(List.of(), pageable, 0);
            String shortResponse = "Short response";

            when(employeeRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            "", "", "", pageable))
                    .thenReturn(employeePage);
            when(restTemplate.getForObject("https://www.google.com", String.class))
                    .thenReturn(shortResponse);

            SearchResponse response = employeeService.searchEmployees("", pageable, "https://www.google.com");

            assertTrue(response.isExternalApiSuccess());
            assertEquals(shortResponse, response.getExternalApiData().get("preview"));
        }

        @Test
        @DisplayName("Should handle null response from external API")
        void shouldHandleNullExternalApiResponse() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Employee> employeePage = new PageImpl<>(List.of(testEmployee), pageable, 1);

            when(employeeRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            "John", "John", "John", pageable))
                    .thenReturn(employeePage);
            when(restTemplate.getForObject("https://www.google.com", String.class))
                    .thenReturn(null);

            SearchResponse response = employeeService.searchEmployees("John", pageable, "https://www.google.com");

            assertNotNull(response);
            assertFalse(response.isExternalApiSuccess());
            assertTrue(response.getExternalApiData().isEmpty());
        }

        @Test
        @DisplayName("Should search with pagination and sorting")
        void shouldSearchWithPagination() {
            Pageable pageable = PageRequest.of(1, 5);
            Employee emp1 = Employee.builder().id(1L).firstName("Alice").build();
            Employee emp2 = Employee.builder().id(2L).firstName("Bob").build();
            Page<Employee> employeePage = new PageImpl<>(List.of(emp1, emp2), pageable, 10);
            String externalApiResponse = "OK";

            when(employeeRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            "A", "A", "A", pageable))
                    .thenReturn(employeePage);
            when(restTemplate.getForObject("https://www.google.com", String.class))
                    .thenReturn(externalApiResponse);

            SearchResponse response = employeeService.searchEmployees("A", pageable, "https://www.google.com");

            assertNotNull(response);
            assertEquals(2, response.getEmployees().getNumberOfElements());
            assertEquals(10, response.getEmployees().getTotalElements());
            assertEquals(1, response.getEmployees().getPageable().getPageNumber());
            assertEquals(5, response.getEmployees().getPageable().getPageSize());
        }
    }
}
