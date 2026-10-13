package com.demo.employeeapp.exception;

import com.demo.employeeapp.dto.EmployeeDTO;
import com.demo.employeeapp.entity.Department;
import com.demo.employeeapp.entity.Employee;
import com.demo.employeeapp.repository.DepartmentRepository;
import com.demo.employeeapp.repository.EmployeeRepository;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        departmentRepository.save(testDepartment);
    }

    @Test
    @DisplayName("Should return 404 with proper error body for EntityNotFoundException - Employee")
    void shouldHandleEntityNotFoundException_Employee() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Employee not found with id: 999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return 404 with proper error body for EntityNotFoundException - Department")
    void shouldHandleEntityNotFoundException_Department() throws Exception {
        mockMvc.perform(get("/api/departments/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Department not found with id: 999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return 400 with proper error body for IllegalArgumentException - Duplicate Employee Email")
    void shouldHandleIllegalArgumentException_DuplicateEmail() throws Exception {
        Employee existing = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department(testDepartment)
                .build();
        employeeRepository.save(existing);

        EmployeeDTO duplicate = EmployeeDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("john.doe@example.com")
                .departmentId(testDepartment.getId())
                .build();

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Employee with email already exists: john.doe@example.com"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return 400 with proper error body for IllegalArgumentException - Duplicate Department Name")
    void shouldHandleIllegalArgumentException_DuplicateDepartmentName() throws Exception {
        Department duplicate = Department.builder()
                .departmentName("Engineering")
                .build();

        mockMvc.perform(post("/api/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Department with name already exists: Engineering"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Should return 404 when employee email not found")
    void shouldHandleEntityNotFoundException_EmployeeByEmail() throws Exception {
        mockMvc.perform(get("/api/employees/search/by-email")
                .param("email", "nonexistent@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Employee not found with email: nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should return 404 when department name not found")
    void shouldHandleEntityNotFoundException_DepartmentByName() throws Exception {
        mockMvc.perform(get("/api/departments/search/by-name")
                .param("departmentName", "Nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Department not found with name: Nonexistent"));
    }
}
