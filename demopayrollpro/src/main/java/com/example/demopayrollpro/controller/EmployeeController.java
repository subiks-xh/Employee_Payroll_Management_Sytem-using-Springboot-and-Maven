package com.example.demopayrollpro.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demopayrollpro.model.Employee;
import com.example.demopayrollpro.service.EmployeeService;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*") // Allow requests from any origin
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Fetching all employees");
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        logger.info("Fetching employee with ID: {}", id);
        try {
            // No need to convert String to Long anymore
            Optional<Employee> employee = employeeService.getEmployeeById(id);
            return employee.map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        logger.warn("Employee with ID: {} not found", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error fetching employee: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
        try {
            logger.info("Creating new employee with ID: {}", employee.getId());

            if (employeeService.employeeExists(employee.getId())) {
                logger.warn("Employee ID: {} already exists", employee.getId());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Employee ID already exists"));
            }

            if (employee.getPassword() == null || employee.getPassword().trim().isEmpty()) {
                employee.setPassword("employee123");
            }

            if (employee.getName() == null || employee.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Employee name is required"));
            }

            Employee savedEmployee = employeeService.saveEmployee(employee);
            logger.info("Employee created successfully: {}", savedEmployee.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
        } catch (Exception e) {
            logger.error("Error creating employee: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create employee: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable String id, @RequestBody Employee employee) {
        try {
            // No need to convert String to Long anymore
            logger.info("Updating employee with ID: {}", id);

            if (!employeeService.employeeExists(id)) {
                logger.warn("Employee with ID: {} not found for update", id);
                return ResponseEntity.notFound().build();
            }

            employee.setId(id);

            if (employee.getPassword() == null || employee.getPassword().trim().isEmpty()) {
                employeeService.getEmployeeById(id).ifPresent(
                        existingEmployee -> employee.setPassword(existingEmployee.getPassword())
                );
            }

            Employee updatedEmployee = employeeService.saveEmployee(employee);
            logger.info("Employee updated successfully: {}", updatedEmployee.getId());
            return ResponseEntity.ok(updatedEmployee);
        } catch (Exception e) {
            logger.error("Error updating employee: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update employee: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String id) {
        try {
            // No need to convert String to Long anymore
            logger.info("Deleting employee with ID: {}", id);

            if (!employeeService.employeeExists(id)) {
                logger.warn("Employee with ID: {} not found for deletion", id);
                return ResponseEntity.notFound().build();
            }

            employeeService.deleteEmployee(id);
            logger.info("Employee deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting employee: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete employee: " + e.getMessage()));
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateEmployee(
            @RequestParam String id,
            @RequestParam String password) {

        try {
            // No need to convert String to Long anymore
            logger.info("Authenticating employee with ID: {}", id);

            Optional<Employee> employeeOpt = employeeService.authenticateEmployee(id, password);

            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();

                Map<String, Object> response = new HashMap<>();
                response.put("id", employee.getId());
                response.put("name", employee.getName());
                response.put("email", employee.getEmail());
                response.put("phone", employee.getPhone());
                response.put("position", employee.getPosition());
                response.put("department", employee.getDepartment());
                response.put("joinDate", employee.getJoinDate());
                response.put("salary", employee.getSalary());

                logger.info("Employee authenticated successfully: {}", id);
                return ResponseEntity.ok(response);
            }

            logger.warn("Authentication failed for employee ID: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            logger.error("Error during authentication: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Authentication error: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchEmployees(@RequestParam String query) {
        try {
            logger.info("Searching employees with query: {}", query);
            List<Employee> matchingEmployees = employeeService.searchEmployees(query);
            return ResponseEntity.ok(matchingEmployees);
        } catch (Exception e) {
            logger.error("Error searching employees: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error searching employees: " + e.getMessage()));
        }
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<?> getEmployeesByDepartment(@PathVariable String department) {
        try {
            logger.info("Fetching employees by department: {}", department);
            List<Employee> employees = employeeService.getEmployeesByDepartment(department);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            logger.error("Error fetching employees by department: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching employees by department: " + e.getMessage()));
        }
    }

    @GetMapping("/check-id/{id}")
    public ResponseEntity<?> checkIdAvailability(@PathVariable String id) {
        try {
            // No need to convert String to Long anymore
            logger.info("Checking availability of employee ID: {}", id);
            boolean isAvailable = !employeeService.employeeExists(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("available", isAvailable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking ID availability: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", "Error checking ID"));
        }
    }
}