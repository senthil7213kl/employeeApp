package com.demo.employeeapp.repository;

import com.demo.employeeapp.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentName(String departmentName);

    @Query("SELECT d FROM Department d WHERE LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :departmentName, '%'))")
    Optional<Department> findByDepartmentNameContainingIgnoreCase(@Param("departmentName") String departmentName);

    boolean existsByDepartmentName(String departmentName);
}
