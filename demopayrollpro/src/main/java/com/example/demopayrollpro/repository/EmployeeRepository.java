package com.example.demopayrollpro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demopayrollpro.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> { // Changed from Long to String
    // Custom query methods can be added here
}