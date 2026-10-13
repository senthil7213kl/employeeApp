package com.demo.employeeapp.service;

import com.demo.employeeapp.dto.DepartmentDTO;
import com.demo.employeeapp.entity.Department;
import com.demo.employeeapp.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceUnitTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private Department testDepartment;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L)
                .departmentName("Engineering")
                .departmentLocation("Building A")
                .departmentDescription("Software Development")
                .build();
    }

    @Nested
    @DisplayName("Get All Departments")
    class GetAllDepartments {

        @Test
        @DisplayName("Should return all departments")
        void shouldReturnAllDepartments() {
            Department dept2 = Department.builder()
                    .id(2L)
                    .departmentName("Marketing")
                    .departmentLocation("Building B")
                    .build();

            when(departmentRepository.findAll()).thenReturn(Arrays.asList(testDepartment, dept2));

            List<Department> result = departmentService.getAllDepartments();

            assertEquals(2, result.size());
            verify(departmentRepository).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no departments exist")
        void shouldReturnEmptyListWhenNoDepartments() {
            when(departmentRepository.findAll()).thenReturn(List.of());

            List<Department> result = departmentService.getAllDepartments();

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Get Department By ID")
    class GetDepartmentById {

        @Test
        @DisplayName("Should return department when found")
        void shouldReturnDepartmentWhenFound() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));

            Department result = departmentService.getDepartmentById(1L);

            assertEquals("Engineering", result.getDepartmentName());
            assertEquals("Building A", result.getDepartmentLocation());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when department not found")
        void shouldThrowWhenDepartmentNotFound() {
            when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> departmentService.getDepartmentById(999L));
        }
    }

    @Nested
    @DisplayName("Get Department By Name")
    class GetDepartmentByName {

        @Test
        @DisplayName("Should return department when found by name")
        void shouldReturnDepartmentWhenFoundByName() {
            when(departmentRepository.findByDepartmentNameContainingIgnoreCase("Engineering"))
                    .thenReturn(Optional.of(testDepartment));

            Department result = departmentService.getDepartmentByName("Engineering");

            assertEquals("Engineering", result.getDepartmentName());
        }

        @Test
        @DisplayName("Should return department when found by partial name")
        void shouldReturnDepartmentWhenFoundByPartialName() {
            when(departmentRepository.findByDepartmentNameContainingIgnoreCase("Human"))
                    .thenReturn(Optional.of(testDepartment));

            Department result = departmentService.getDepartmentByName("Human");

            assertEquals("Engineering", result.getDepartmentName());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when department not found by name")
        void shouldThrowWhenDepartmentNotFoundByName() {
            when(departmentRepository.findByDepartmentNameContainingIgnoreCase("Nonexistent"))
                    .thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    departmentService.getDepartmentByName("Nonexistent"));
        }
    }

    @Nested
    @DisplayName("Create Department")
    class CreateDepartment {

        @Test
        @DisplayName("Should create department successfully")
        void shouldCreateDepartmentSuccessfully() {
            DepartmentDTO dto = DepartmentDTO.builder()
                    .departmentName("HR")
                    .departmentLocation("Building C")
                    .departmentDescription("Human Resources")
                    .build();

            when(departmentRepository.existsByDepartmentName("HR")).thenReturn(false);
            when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> {
                Department saved = invocation.getArgument(0);
                return saved;
            });

            Department result = departmentService.createDepartment(dto);

            assertEquals("HR", result.getDepartmentName());
            assertEquals("Building C", result.getDepartmentLocation());
            assertEquals("Human Resources", result.getDepartmentDescription());
            verify(departmentRepository).save(any(Department.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when department name already exists")
        void shouldThrowWhenDepartmentNameAlreadyExists() {
            DepartmentDTO dto = DepartmentDTO.builder()
                    .departmentName("Engineering")
                    .build();

            when(departmentRepository.existsByDepartmentName("Engineering")).thenReturn(true);

            assertThrows(IllegalArgumentException.class, () ->
                    departmentService.createDepartment(dto));
        }
    }

    @Nested
    @DisplayName("Update Department")
    class UpdateDepartment {

        @Test
        @DisplayName("Should update department successfully")
        void shouldUpdateDepartmentSuccessfully() {
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(departmentRepository.save(any(Department.class))).thenReturn(testDepartment);

            DepartmentDTO dto = DepartmentDTO.builder()
                    .departmentName("Updated Engineering")
                    .departmentLocation("Building D")
                    .departmentDescription("Updated Description")
                    .build();

            Department result = departmentService.updateDepartment(1L, dto);

            assertEquals("Updated Engineering", result.getDepartmentName());
            verify(departmentRepository).save(testDepartment);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when department not found")
        void shouldThrowWhenDepartmentNotFound() {
            when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

            DepartmentDTO dto = DepartmentDTO.builder()
                    .departmentName("Test")
                    .build();

            assertThrows(EntityNotFoundException.class, () ->
                    departmentService.updateDepartment(999L, dto));
        }
    }

    @Nested
    @DisplayName("Delete Department")
    class DeleteDepartment {

        @Test
        @DisplayName("Should delete department successfully")
        void shouldDeleteDepartmentSuccessfully() {
            when(departmentRepository.existsById(1L)).thenReturn(true);
            doNothing().when(departmentRepository).deleteById(1L);

            assertDoesNotThrow(() -> departmentService.deleteDepartment(1L));
            verify(departmentRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when department not found")
        void shouldThrowWhenDepartmentNotFound() {
            when(departmentRepository.existsById(999L)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () ->
                    departmentService.deleteDepartment(999L));
        }
    }
}
