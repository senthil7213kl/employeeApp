package com.demo.employeeapp.controller;

import com.demo.employeeapp.dto.DepartmentRequestDTO;
import com.demo.employeeapp.entity.Department;
import com.demo.employeeapp.repository.DepartmentRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DepartmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        departmentRepository.save(testDepartment);
    }

    @Nested
    @DisplayName("GET /api/departments")
    class GetAllDepartments {

        @Test
        @DisplayName("Should return all departments with 200 OK")
        void shouldReturnAllDepartments() throws Exception {
            mockMvc.perform(get("/api/departments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].departmentName").value("Engineering"));
        }

        @Test
        @DisplayName("Should return empty list when no departments exist")
        void shouldReturnEmptyList() throws Exception {
            departmentRepository.deleteAll();

            mockMvc.perform(get("/api/departments"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/departments/{id}")
    class GetDepartmentById {

        @Test
        @DisplayName("Should return department by ID with 200 OK")
        void shouldReturnDepartmentById() throws Exception {
            mockMvc.perform(get("/api/departments/{id}", testDepartment.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.departmentName").value("Engineering"))
                    .andExpect(jsonPath("$.departmentLocation").value("Building A"));
        }

        @Test
        @DisplayName("Should return 404 when department not found")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/departments/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }
    }

    @Nested
    @DisplayName("GET /api/departments/search/by-name")
    class GetDepartmentByName {

        @Test
        @DisplayName("Should return department by name with 200 OK")
        void shouldReturnDepartmentByName() throws Exception {
            mockMvc.perform(get("/api/departments/search/by-name")
                    .param("departmentName", "Engineering"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.departmentName").value("Engineering"));
        }

        @Test
        @DisplayName("Should return department by partial name with 200 OK")
        void shouldReturnDepartmentByPartialName() throws Exception {
            mockMvc.perform(get("/api/departments/search/by-name")
                    .param("departmentName", "Eng"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.departmentName").value("Engineering"));
        }

        @Test
        @DisplayName("Should return department by multi-word name with 200 OK")
        void shouldReturnDepartmentByMultiWordName() throws Exception {
            Department multiWordDept = Department.builder()
                    .departmentName("Human Resources")
                    .departmentLocation("Building C")
                    .departmentDescription("Human Resources and Recruitment")
                    .build();
            departmentRepository.save(multiWordDept);

            mockMvc.perform(get("/api/departments/search/by-name")
                    .param("departmentName", "Human Resources"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.departmentName").value("Human Resources"));
        }

        @Test
        @DisplayName("Should handle split multi-word name parameters")
        void shouldHandleSplitMultiWordNameParameters() throws Exception {
            Department multiWordDept = Department.builder()
                    .departmentName("Human Resources")
                    .departmentLocation("Building C")
                    .departmentDescription("Human Resources and Recruitment")
                    .build();
            departmentRepository.save(multiWordDept);

            mockMvc.perform(get("/api/departments/search/by-name")
                    .param("departmentName", "Human")
                    .param("departmentName", "Resources"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.departmentName").value("Human Resources"));
        }

        @Test
        @DisplayName("Should return 404 when department not found by name")
        void shouldReturn404WhenNotFoundByName() throws Exception {
            mockMvc.perform(get("/api/departments/search/by-name")
                    .param("departmentName", "Nonexistent"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("POST /api/departments")
    class CreateDepartment {

        @Test
        @DisplayName("Should create department with 201 Created")
        void shouldCreateDepartment() throws Exception {
            DepartmentRequestDTO newDept = DepartmentRequestDTO.builder()
                    .departmentName("HR")
                    .departmentLocation("Building C")
                    .departmentDescription("Human Resources")
                    .build();

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newDept)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.departmentName").value("HR"))
                    .andExpect(jsonPath("$.departmentLocation").value("Building C"));
        }

        @Test
        @DisplayName("Should return 400 when department name already exists")
        void shouldReturn400WhenNameExists() throws Exception {
            DepartmentRequestDTO duplicate = DepartmentRequestDTO.builder()
                    .departmentName("Engineering")
                    .build();

            mockMvc.perform(post("/api/departments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(duplicate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }
    }

    @Nested
    @DisplayName("PUT /api/departments/{id}")
    class UpdateDepartment {

        @Test
        @DisplayName("Should update department with 200 OK")
        void shouldUpdateDepartment() throws Exception {
            DepartmentRequestDTO updated = DepartmentRequestDTO.builder()
                    .departmentName("Updated Engineering")
                    .departmentLocation("Building D")
                    .departmentDescription("Updated Description")
                    .build();

            mockMvc.perform(put("/api/departments/{id}", testDepartment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.departmentName").value("Updated Engineering"))
                    .andExpect(jsonPath("$.departmentLocation").value("Building D"));
        }

        @Test
        @DisplayName("Should return 404 when department not found")
        void shouldReturn404WhenNotFound() throws Exception {
            DepartmentRequestDTO updated = DepartmentRequestDTO.builder()
                    .departmentName("Updated")
                    .build();

            mockMvc.perform(put("/api/departments/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("DELETE /api/departments/{id}")
    class DeleteDepartment {

        @Test
        @DisplayName("Should delete department with 204 No Content")
        void shouldDeleteDepartment() throws Exception {
            mockMvc.perform(delete("/api/departments/{id}", testDepartment.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/departments/{id}", testDepartment.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when department not found")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(delete("/api/departments/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
