package com.example.demopayrollpro.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demopayrollpro.model.Employee;
import com.example.demopayrollpro.model.Payroll;
import com.example.demopayrollpro.repository.EmployeeRepository;
import com.example.demopayrollpro.repository.PayrollRepository;

@Service
public class PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    public List<Payroll> getAllPayrolls() {
        return payrollRepository.findAll();
    }
    
    public Optional<Payroll> getPayrollById(Long id) {
        return payrollRepository.findById(id);
    }
    
    public List<Payroll> getPayrollsByEmployee(String employeeId) { // Changed from Long to String
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        return employee.map(payrollRepository::findByEmployee).orElse(List.of());
    }
    
    public List<Payroll> getPayrollsByMonth(YearMonth month) {
        return payrollRepository.findByMonth(month);
    }
    
    public Optional<Payroll> getPayrollByEmployeeAndMonth(String employeeId, YearMonth month) { // Changed from Long to String
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        return employee.flatMap(emp -> payrollRepository.findByEmployeeAndMonth(emp, month));
    }
    
    public Payroll savePayroll(Payroll payroll) {
        if (payroll.getCreatedDate() == null) {
    LocalDate now = LocalDate.now();
    payroll.setCreatedDate(now);
}
        System.out.println("Saving payroll: " + payroll);
        return payrollRepository.save(payroll);
    }
    
    public void deletePayroll(Long id) {
        payrollRepository.deleteById(id);
    }
}