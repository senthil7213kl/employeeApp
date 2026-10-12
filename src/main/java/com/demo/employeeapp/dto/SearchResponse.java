package com.demo.employeeapp.dto;

import com.demo.employeeapp.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    private Page<Employee> employees;
    private Map<String, Object> externalApiData;
    private String externalApiUrl;
    private boolean externalApiSuccess;
}
