package com.demo.employeeapp.service;

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
        return departmentRepository.findByDepartmentName(departmentName)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with name: " + departmentName));
    }

    public Department createDepartment(Department department) {
        if (departmentRepository.existsByDepartmentName(department.getDepartmentName())) {
            throw new IllegalArgumentException("Department with name already exists: " + department.getDepartmentName());
        }
        department.setId(null);
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, Department departmentDetails) {
        Department department = getDepartmentById(id);
        department.setDepartmentName(departmentDetails.getDepartmentName());
        department.setDepartmentLocation(departmentDetails.getDepartmentLocation());
        department.setDepartmentDescription(departmentDetails.getDepartmentDescription());
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }
}
