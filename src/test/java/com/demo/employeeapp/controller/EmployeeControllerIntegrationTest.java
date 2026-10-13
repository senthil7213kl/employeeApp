package com.demo.employeeapp.controller;

import com.demo.employeeapp.dto.EmployeeDTO;
import com.demo.employeeapp.entity.Department;
import com.demo.employeeapp.entity.Employee;
import com.demo.employeeapp.repository.DepartmentRepository;
import com.demo.employeeapp.repository.EmployeeRepository;
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
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        testDepartment = Department.builder()
                .departmentName("Engineering")
                .departmentLocation("Building A")
                .departmentDescription("Software Development")
                .build();
        departmentRepository.save(testDepartment);

        testEmployee = Employee.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .position("Software Engineer")
                .salary(80000.0)
                .department(testDepartment)
                .build();
        employeeRepository.save(testEmployee);
    }

    @Nested
    @DisplayName("GET /api/employees")
    class GetAllEmployees {

        @Test
        @DisplayName("Should return all employees with 200 OK")
        void shouldReturnAllEmployees() throws Exception {
            mockMvc.perform(get("/api/employees"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].firstName").value("John"))
                    .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("Should return empty list when no employees exist")
        void shouldReturnEmptyList() throws Exception {
            employeeRepository.deleteAll();

            mockMvc.perform(get("/api/employees"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/employees/{id}")
    class GetEmployeeById {

        @Test
        @DisplayName("Should return employee by ID with 200 OK")
        void shouldReturnEmployeeById() throws Exception {
            mockMvc.perform(get("/api/employees/{id}", testEmployee.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.lastName").value("Doe"))
                    .andExpect(jsonPath("$.email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("Should return 404 when employee not found")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(get("/api/employees/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"));
        }
    }

    @Nested
    @DisplayName("GET /api/employees/search/by-email")
    class GetEmployeeByEmail {

        @Test
        @DisplayName("Should return employee by email with 200 OK")
        void shouldReturnEmployeeByEmail() throws Exception {
            mockMvc.perform(get("/api/employees/search/by-email")
                    .param("email", "john.doe@example.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("Should return 404 when employee not found by email")
        void shouldReturn404WhenNotFoundByEmail() throws Exception {
            mockMvc.perform(get("/api/employees/search/by-email")
                    .param("email", "nonexistent@example.com"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /api/employees/department/{departmentId}")
    class GetEmployeesByDepartment {

        @Test
        @DisplayName("Should return employees by department ID")
        void shouldReturnEmployeesByDepartment() throws Exception {
            Employee employee2 = Employee.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("jane.smith@example.com")
                    .department(testDepartment)
                    .build();
            employeeRepository.save(employee2);

            mockMvc.perform(get("/api/employees/department/{departmentId}", testDepartment.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("Should return empty list when no employees in department")
        void shouldReturnEmptyListWhenNoEmployees() throws Exception {
            mockMvc.perform(get("/api/employees/department/{departmentId}", 999L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("POST /api/employees")
    class CreateEmployee {

        @Test
        @DisplayName("Should create employee with 201 Created")
        void shouldCreateEmployee() throws Exception {
            EmployeeDTO newEmployee = EmployeeDTO.builder()
                    .firstName("New")
                    .lastName("User")
                    .email("new.user@example.com")
                    .phone("555-555-5555")
                    .position("Developer")
                    .salary(75000.0)
                    .departmentId(testDepartment.getId())
                    .build();

            mockMvc.perform(post("/api/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newEmployee)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.firstName").value("New"))
                    .andExpect(jsonPath("$.lastName").value("User"))
                    .andExpect(jsonPath("$.email").value("new.user@example.com"));
        }

        @Test
        @DisplayName("Should return 400 when email already exists")
        void shouldReturn400WhenEmailExists() throws Exception {
            EmployeeDTO duplicate = EmployeeDTO.builder()
                    .firstName("Duplicate")
                    .lastName("User")
                    .email("john.doe@example.com")
                    .departmentId(testDepartment.getId())
                    .build();

            mockMvc.perform(post("/api/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(duplicate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("Should return 404 when department not found")
        void shouldReturn404WhenDepartmentNotFound() throws Exception {
            EmployeeDTO newEmployee = EmployeeDTO.builder()
                    .firstName("New")
                    .lastName("User")
                    .email("new.user@example.com")
                    .departmentId(999L)
                    .build();

            mockMvc.perform(post("/api/employees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newEmployee)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("PUT /api/employees/{id}")
    class UpdateEmployee {

        @Test
        @DisplayName("Should update employee with 200 OK")
        void shouldUpdateEmployee() throws Exception {
            Employee updated = Employee.builder()
                    .firstName("Updated")
                    .lastName("Name")
                    .email("updated@example.com")
                    .phone("999-999-9999")
                    .position("Senior Engineer")
                    .salary(90000.0)
                    .department(testDepartment)
                    .build();

            mockMvc.perform(put("/api/employees/{id}", testEmployee.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value("Updated"))
                    .andExpect(jsonPath("$.lastName").value("Name"));
        }

        @Test
        @DisplayName("Should return 404 when employee not found")
        void shouldReturn404WhenNotFound() throws Exception {
            Employee updated = Employee.builder()
                    .firstName("Updated")
                    .email("updated@example.com")
                    .department(testDepartment)
                    .build();

            mockMvc.perform(put("/api/employees/{id}", 999L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updated)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("DELETE /api/employees/{id}")
    class DeleteEmployee {

        @Test
        @DisplayName("Should delete employee with 204 No Content")
        void shouldDeleteEmployee() throws Exception {
            mockMvc.perform(delete("/api/employees/{id}", testEmployee.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/employees/{id}", testEmployee.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 when employee not found")
        void shouldReturn404WhenNotFound() throws Exception {
            mockMvc.perform(delete("/api/employees/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /api/employees/search")
    class SearchEmployees {

        @Test
        @DisplayName("Should search employees by keyword with pagination")
        void shouldSearchEmployeesByKeyword() throws Exception {
            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "John")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees.content").isNotEmpty())
                    .andExpect(jsonPath("$.employees.content[0].firstName").value("John"))
                    .andExpect(jsonPath("$.externalApiUrl").exists())
                    .andExpect(jsonPath("$.externalApiSuccess").exists());
        }

        @Test
        @DisplayName("Should return empty results when keyword not found")
        void shouldReturnEmptyResultsWhenKeywordNotFound() throws Exception {
            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "Nonexistent")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees.content", hasSize(0)))
                    .andExpect(jsonPath("$.employees.empty").value(true));
        }

        @Test
        @DisplayName("Should search by last name")
        void shouldSearchByLastName() throws Exception {
            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "Doe")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees.content").isNotEmpty())
                    .andExpect(jsonPath("$.employees.content[0].lastName").value("Doe"));
        }

        @Test
        @DisplayName("Should search by email")
        void shouldSearchByEmail() throws Exception {
            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "john.doe")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees.content").isNotEmpty())
                    .andExpect(jsonPath("$.employees.content[0].email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("Should support sorting by field")
        void shouldSupportSorting() throws Exception {
            Employee employee2 = Employee.builder()
                    .firstName("Alice")
                    .lastName("Smith")
                    .email("alice.smith@example.com")
                    .department(testDepartment)
                    .build();
            employeeRepository.save(employee2);

            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "firstName")
                    .param("direction", "ASC"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees.content", hasSize(2)))
                    .andExpect(jsonPath("$.employees.content[0].firstName").value("Alice"));
        }

        @Test
        @DisplayName("Should support descending sort direction")
        void shouldSupportDescendingSort() throws Exception {
            Employee employee2 = Employee.builder()
                    .firstName("Alice")
                    .lastName("Smith")
                    .email("alice.smith@example.com")
                    .department(testDepartment)
                    .build();
            employeeRepository.save(employee2);

            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "firstName")
                    .param("direction", "DESC"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees.content", hasSize(2)))
                    .andExpect(jsonPath("$.employees.content[0].firstName").value("John"));
        }

        @Test
        @DisplayName("Should use default values for pagination parameters")
        void shouldUseDefaultPaginationValues() throws Exception {
            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "John"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees").exists())
                    .andExpect(jsonPath("$.externalApiUrl").value("https://www.google.com"));
        }

        @Test
        @DisplayName("Should handle external API failure gracefully")
        void shouldHandleExternalApiFailure() throws Exception {
            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "John")
                    .param("externalApiUrl", "https://invalid.url.that.does.not.exist"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees").exists())
                    .andExpect(jsonPath("$.externalApiSuccess").value(false))
                    .andExpect(jsonPath("$.externalApiData.status").value("error"));
        }

        @Test
        @DisplayName("Should return paginated results with correct page info")
        void shouldReturnPaginatedResults() throws Exception {
            for (int i = 0; i < 15; i++) {
                Employee emp = Employee.builder()
                        .firstName("User" + i)
                        .lastName("Test")
                        .email("user" + i + "@example.com")
                        .department(testDepartment)
                        .build();
                employeeRepository.save(emp);
            }

            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "User")
                    .param("page", "0")
                    .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees.content", hasSize(5)))
                    .andExpect(jsonPath("$.employees.number").value(0))
                    .andExpect(jsonPath("$.employees.size").value(5))
                    .andExpect(jsonPath("$.employees.totalPages").value(3))
                    .andExpect(jsonPath("$.employees.totalElements").value(15));
        }

        @Test
        @DisplayName("Should return second page of results")
        void shouldReturnSecondPage() throws Exception {
            for (int i = 0; i < 10; i++) {
                Employee emp = Employee.builder()
                        .firstName("User" + i)
                        .lastName("Test")
                        .email("user" + i + "@example.com")
                        .department(testDepartment)
                        .build();
                employeeRepository.save(emp);
            }

            mockMvc.perform(get("/api/employees/search")
                    .param("keyword", "User")
                    .param("page", "1")
                    .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.employees.content", hasSize(5)))
                    .andExpect(jsonPath("$.employees.number").value(1))
                    .andExpect(jsonPath("$.employees.first").value(false))
                    .andExpect(jsonPath("$.employees.last").value(true));
        }
    }
}
