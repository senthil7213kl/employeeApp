package com.demo.employeeapp.repository;

import com.demo.employeeapp.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DepartmentRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();

        testDepartment = Department.builder()
                .departmentName("Engineering")
                .departmentLocation("Building A")
                .departmentDescription("Software Development")
                .build();
        entityManager.persist(testDepartment);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should save and find department by ID")
    void shouldSaveAndFindDepartmentById() {
        Optional<Department> found = departmentRepository.findById(testDepartment.getId());

        assertTrue(found.isPresent());
        assertEquals("Engineering", found.get().getDepartmentName());
        assertEquals("Building A", found.get().getDepartmentLocation());
    }

    @Test
    @DisplayName("Should find department by name")
    void shouldFindDepartmentByName() {
        Optional<Department> found = departmentRepository.findByDepartmentName("Engineering");

        assertTrue(found.isPresent());
        assertEquals("Engineering", found.get().getDepartmentName());
    }

    @Test
    @DisplayName("Should return empty when department name not found")
    void shouldReturnEmptyWhenNameNotFound() {
        Optional<Department> found = departmentRepository.findByDepartmentName("Nonexistent");
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should find department by partial name containing")
    void shouldFindDepartmentByPartialNameContaining() {
        Optional<Department> found = departmentRepository.findByDepartmentNameContainingIgnoreCase("Eng");

        assertTrue(found.isPresent());
        assertEquals("Engineering", found.get().getDepartmentName());
    }

    @Test
    @DisplayName("Should find department by partial name case insensitive")
    void shouldFindDepartmentByPartialNameCaseInsensitive() {
        Optional<Department> found = departmentRepository.findByDepartmentNameContainingIgnoreCase("engineering");

        assertTrue(found.isPresent());
        assertEquals("Engineering", found.get().getDepartmentName());
    }

    @Test
    @DisplayName("Should find multi-word department by partial name")
    void shouldFindMultiWordDepartmentByPartialName() {
        Department multiWordDept = Department.builder()
                .departmentName("Human Resources")
                .departmentLocation("Building C")
                .build();
        entityManager.persist(multiWordDept);
        entityManager.flush();

        Optional<Department> found = departmentRepository.findByDepartmentNameContainingIgnoreCase("Human");

        assertTrue(found.isPresent());
        assertEquals("Human Resources", found.get().getDepartmentName());
    }

    @Test
    @DisplayName("Should check if department name exists")
    void shouldCheckIfDepartmentNameExists() {
        assertTrue(departmentRepository.existsByDepartmentName("Engineering"));
        assertFalse(departmentRepository.existsByDepartmentName("Nonexistent"));
    }

    @Test
    @DisplayName("Should find all departments")
    void shouldFindAllDepartments() {
        Department dept2 = Department.builder()
                .departmentName("Marketing")
                .departmentLocation("Building B")
                .build();
        entityManager.persist(dept2);
        entityManager.flush();

        List<Department> all = departmentRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Should delete department by ID")
    void shouldDeleteDepartmentById() {
        Long id = testDepartment.getId();

        departmentRepository.deleteById(id);
        entityManager.flush();

        Optional<Department> found = departmentRepository.findById(id);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should check existsById correctly")
    void shouldCheckExistsById() {
        assertTrue(departmentRepository.existsById(testDepartment.getId()));
        assertFalse(departmentRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should update department")
    void shouldUpdateDepartment() {
        testDepartment.setDepartmentName("Updated Engineering");
        testDepartment.setDepartmentLocation("Building D");
        entityManager.flush();

        Optional<Department> found = departmentRepository.findById(testDepartment.getId());

        assertTrue(found.isPresent());
        assertEquals("Updated Engineering", found.get().getDepartmentName());
        assertEquals("Building D", found.get().getDepartmentLocation());
    }
}
