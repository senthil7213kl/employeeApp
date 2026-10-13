package com.demo.employeeapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    @NotBlank(message = "Department name is required")
    private String departmentName;

    private String departmentLocation;

    private String departmentDescription;
}
