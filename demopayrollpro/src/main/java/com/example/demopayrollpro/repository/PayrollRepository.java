package com.example.demopayrollpro.repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demopayrollpro.model.Employee;
import com.example.demopayrollpro.model.Payroll;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    
    List<Payroll> findByEmployee(Employee employee);
    
    List<Payroll> findByMonth(YearMonth month);
    
    Optional<Payroll> findByEmployeeAndMonth(Employee employee, YearMonth month);
}
