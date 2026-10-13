package com.demo.employeeapp.service;

import com.demo.employeeapp.dto.DepartmentDTO;
import com.demo.employeeapp.entity.Department;
import com.demo.employeeapp.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
    }

    public Department getDepartmentByName(String departmentName) {
        return departmentRepository.findByDepartmentNameContainingIgnoreCase(departmentName)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with name: " + departmentName));
    }

    public Department createDepartment(DepartmentDTO dto) {
        if (departmentRepository.existsByDepartmentName(dto.getDepartmentName())) {
            throw new IllegalArgumentException("Department with name already exists: " + dto.getDepartmentName());
        }
        Department department = Department.builder()
                .departmentName(dto.getDepartmentName())
                .departmentLocation(dto.getDepartmentLocation())
                .departmentDescription(dto.getDepartmentDescription())
                .build();
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, DepartmentDTO dto) {
        Department department = getDepartmentById(id);
        department.setDepartmentName(dto.getDepartmentName());
        department.setDepartmentLocation(dto.getDepartmentLocation());
        department.setDepartmentDescription(dto.getDepartmentDescription());
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }
}
