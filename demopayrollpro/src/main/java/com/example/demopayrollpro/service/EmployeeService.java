package com.example.demopayrollpro.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demopayrollpro.model.Employee;
import com.example.demopayrollpro.repository.EmployeeRepository;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(String id) { // Changed from Long to String
        return employeeRepository.findById(id);
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(String id) { // Changed from Long to String
        employeeRepository.deleteById(id);
    }

    public boolean employeeExists(String id) { // Changed from Long to String
        return employeeRepository.existsById(id);
    }

    public Optional<Employee> authenticateEmployee(String id, String password) { // Changed from Long to String
        Optional<Employee> employeeOpt = employeeRepository.findById(id);

        if (employeeOpt.isPresent() && employeeOpt.get().getPassword().equals(password)) {
            return employeeOpt;
        }

        return Optional.empty();
    }

    public List<Employee> searchEmployees(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllEmployees();
        }

        String lowercaseQuery = query.toLowerCase().trim();

        return employeeRepository.findAll().stream()
                .filter(employee ->
                        (employee.getName() != null && employee.getName().toLowerCase().contains(lowercaseQuery)) ||
                        (employee.getDepartment() != null && employee.getDepartment().toLowerCase().contains(lowercaseQuery)) ||
                        (employee.getPosition() != null && employee.getPosition().toLowerCase().contains(lowercaseQuery))
                )
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String lowercaseDepartment = department.toLowerCase().trim();

        return employeeRepository.findAll().stream()
                .filter(employee ->
                        employee.getDepartment() != null &&
                        employee.getDepartment().toLowerCase().equals(lowercaseDepartment)
                )
                .collect(Collectors.toList());
    }
}