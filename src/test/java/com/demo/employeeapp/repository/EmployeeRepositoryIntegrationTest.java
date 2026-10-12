package com.demo.employeeapp.repository;

import com.demo.employeeapp.entity.Department;
import com.demo.employeeapp.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        testDepartment = Department.builder()
                .departmentName("Engineering")
                .departmentLocation("Building A")
                .build();
        entityManager.persist(testDepartment);
    }

    @Test
    @DisplayName("Should save and find employee by ID")
    void shouldSaveAndFindEmployeeById() {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .position("Software Engineer")
                .salary(80000.0)
                .department(testDepartment)
                .build();
        entityManager.persist(employee);
        entityManager.flush();

        Optional<Employee> found = employeeRepository.findById(employee.getId());

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
        assertEquals("john.doe@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should find employee by email")
    void shouldFindEmployeeByEmail() {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(employee);
        entityManager.flush();

        Optional<Employee> found = employeeRepository.findByEmail("john.doe@example.com");

        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<Employee> found = employeeRepository.findByEmail("nonexistent@example.com");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(employee);
        entityManager.flush();

        assertTrue(employeeRepository.existsByEmail("john.doe@example.com"));
        assertFalse(employeeRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should find employees by department ID")
    void shouldFindEmployeesByDepartmentId() {
        Employee employee1 = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .department(testDepartment)
                .build();
        Employee employee2 = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.flush();

        List<Employee> employees = employeeRepository.findByDepartmentId(testDepartment.getId());

        assertEquals(2, employees.size());
    }

    @Test
    @DisplayName("Should return empty list when no employees in department")
    void shouldReturnEmptyWhenNoEmployeesInDepartment() {
        List<Employee> employees = employeeRepository.findByDepartmentId(999L);
        assertTrue(employees.isEmpty());
    }

    @Test
    @DisplayName("Should find all employees")
    void shouldFindAllEmployees() {
        Employee employee1 = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .department(testDepartment)
                .build();
        Employee employee2 = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.flush();

        List<Employee> all = employeeRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Should delete employee by ID")
    void shouldDeleteEmployeeById() {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(employee);
        entityManager.flush();
        Long id = employee.getId();

        employeeRepository.deleteById(id);
        entityManager.flush();

        Optional<Employee> found = employeeRepository.findById(id);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should check existsById correctly")
    void shouldCheckExistsById() {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(employee);
        entityManager.flush();

        assertTrue(employeeRepository.existsById(employee.getId()));
        assertFalse(employeeRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should search employees by keyword with pagination")
    void shouldSearchEmployeesByKeywordWithPagination() {
        Employee employee1 = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department(testDepartment)
                .build();
        Employee employee2 = Employee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .department(testDepartment)
                .build();
        Employee employee3 = Employee.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .email("bob.johnson@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(employee1);
        entityManager.persist(employee2);
        entityManager.persist(employee3);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> result = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "John", "John", "John", pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(e -> e.getFirstName().equals("John")));
        assertTrue(result.getContent().stream().anyMatch(e -> e.getLastName().equals("Johnson")));
    }

    @Test
    @DisplayName("Should return empty page when keyword not found")
    void shouldReturnEmptyPageWhenKeywordNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> result = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "Nonexistent", "Nonexistent", "Nonexistent", pageable);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    @DisplayName("Should search by email with pagination")
    void shouldSearchByEmailWithPagination() {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(employee);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> result = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "john.doe", "john.doe", "john.doe", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("john.doe@example.com", result.getContent().get(0).getEmail());
    }

    @Test
    @DisplayName("Should support pagination with limited page size")
    void shouldSupportPaginationWithLimitedPageSize() {
        for (int i = 0; i < 8; i++) {
            Employee emp = Employee.builder()
                    .firstName("User" + i)
                    .lastName("Test")
                    .email("user" + i + "@example.com")
                    .department(testDepartment)
                    .build();
            entityManager.persist(emp);
        }
        entityManager.flush();

        Pageable page0 = PageRequest.of(0, 3);
        Page<Employee> result0 = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "User", "User", "User", page0);

        assertEquals(3, result0.getContent().size());
        assertEquals(8, result0.getTotalElements());
        assertEquals(3, result0.getTotalPages());
        assertTrue(result0.isFirst());
        assertFalse(result0.isLast());

        Pageable page1 = PageRequest.of(1, 3);
        Page<Employee> result1 = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "User", "User", "User", page1);

        assertEquals(3, result1.getContent().size());
        assertFalse(result1.isFirst());
        assertFalse(result1.isLast());

        Pageable page2 = PageRequest.of(2, 3);
        Page<Employee> result2 = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "User", "User", "User", page2);

        assertEquals(2, result2.getContent().size());
        assertTrue(result2.isLast());
    }

    @Test
    @DisplayName("Should support sorting in search results")
    void shouldSupportSortingInSearchResults() {
        Employee emp1 = Employee.builder()
                .firstName("Charlie")
                .lastName("Last")
                .email("charlie@example.com")
                .department(testDepartment)
                .build();
        Employee emp2 = Employee.builder()
                .firstName("Alice")
                .lastName("Last")
                .email("alice@example.com")
                .department(testDepartment)
                .build();
        Employee emp3 = Employee.builder()
                .firstName("Bob")
                .lastName("Last")
                .email("bob@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(emp1);
        entityManager.persist(emp2);
        entityManager.persist(emp3);
        entityManager.flush();

        Pageable ascSort = PageRequest.of(0, 10, Sort.by("firstName").ascending());
        Page<Employee> ascResult = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "", "", "", ascSort);

        assertEquals(3, ascResult.getContent().size());
        assertEquals("Alice", ascResult.getContent().get(0).getFirstName());
        assertEquals("Bob", ascResult.getContent().get(1).getFirstName());
        assertEquals("Charlie", ascResult.getContent().get(2).getFirstName());

        Pageable descSort = PageRequest.of(0, 10, Sort.by("firstName").descending());
        Page<Employee> descResult = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "", "", "", descSort);

        assertEquals("Charlie", descResult.getContent().get(0).getFirstName());
        assertEquals("Bob", descResult.getContent().get(1).getFirstName());
        assertEquals("Alice", descResult.getContent().get(2).getFirstName());
    }

    @Test
    @DisplayName("Should perform case-insensitive search")
    void shouldPerformCaseInsensitiveSearch() {
        Employee employee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department(testDepartment)
                .build();
        entityManager.persist(employee);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Employee> result1 = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "john", "john", "john", pageable);
        assertEquals(1, result1.getTotalElements());

        Page<Employee> result2 = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "JOHN", "JOHN", "JOHN", pageable);
        assertEquals(1, result2.getTotalElements());

        Page<Employee> result3 = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        "DoE", "DoE", "DoE", pageable);
        assertEquals(1, result3.getTotalElements());
    }
}
