package com.demo.employeeapp.service;

import com.demo.employeeapp.dto.DepartmentRequestDTO;
import com.demo.employeeapp.dto.DepartmentResponseDTO;
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

    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        return toResponseDTO(department);
    }

    public DepartmentResponseDTO getDepartmentByName(String departmentName) {
        Department department = departmentRepository.findByDepartmentNameContainingIgnoreCase(departmentName)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with name: " + departmentName));
        return toResponseDTO(department);
    }

    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto) {
        if (departmentRepository.existsByDepartmentName(dto.getDepartmentName())) {
            throw new IllegalArgumentException("Department with name already exists: " + dto.getDepartmentName());
        }
        Department department = Department.builder()
                .departmentName(dto.getDepartmentName())
                .departmentLocation(dto.getDepartmentLocation())
                .departmentDescription(dto.getDepartmentDescription())
                .build();
        return toResponseDTO(departmentRepository.save(department));
    }

    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto) {
        Department department = getDepartmentByIdEntity(id);
        department.setDepartmentName(dto.getDepartmentName());
        department.setDepartmentLocation(dto.getDepartmentLocation());
        department.setDepartmentDescription(dto.getDepartmentDescription());
        return toResponseDTO(departmentRepository.save(department));
    }

    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private Department getDepartmentByIdEntity(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
    }

    private DepartmentResponseDTO toResponseDTO(Department department) {
        return DepartmentResponseDTO.builder()
                .id(department.getId())
                .departmentName(department.getDepartmentName())
                .departmentLocation(department.getDepartmentLocation())
                .departmentDescription(department.getDepartmentDescription())
                .build();
    }
}
