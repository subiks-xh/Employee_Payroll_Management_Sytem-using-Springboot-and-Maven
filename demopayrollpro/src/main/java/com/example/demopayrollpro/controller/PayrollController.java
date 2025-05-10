package com.example.demopayrollpro.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demopayrollpro.model.Employee;
import com.example.demopayrollpro.model.Payroll;
import com.example.demopayrollpro.service.EmployeeService;
import com.example.demopayrollpro.service.PayrollService;

@RestController
@RequestMapping("/api/payrolls")
@CrossOrigin(origins = "*")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<Payroll>> getAllPayrolls() {
        return ResponseEntity.ok(payrollService.getAllPayrolls());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payroll> getPayrollById(@PathVariable Long id) {
        return payrollService.getPayrollById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Payroll>> getPayrollsByEmployee(@PathVariable String employeeId) { // Changed from Long to String
        return ResponseEntity.ok(payrollService.getPayrollsByEmployee(employeeId));
    }

    @GetMapping("/month/{month}")
    public ResponseEntity<List<Payroll>> getPayrollsByMonth(@PathVariable String month) {
        try {
            YearMonth yearMonth = YearMonth.parse(month);
            return ResponseEntity.ok(payrollService.getPayrollsByMonth(yearMonth));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/employee/{employeeId}/month/{month}")
    public ResponseEntity<Payroll> getPayrollByEmployeeAndMonth(
            @PathVariable String employeeId, // Changed from Long to String
            @PathVariable String month) {
        try {
            YearMonth yearMonth = YearMonth.parse(month);
            return payrollService.getPayrollByEmployeeAndMonth(employeeId, yearMonth)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
public ResponseEntity<?> createPayroll(@RequestBody Map<String, Object> payrollRequest) {
    System.out.println("Incoming payroll request: " + payrollRequest);
    try {
        // Validate required fields
        if (!payrollRequest.containsKey("empId") || !payrollRequest.containsKey("month") ||
            !payrollRequest.containsKey("base") || !payrollRequest.containsKey("amount")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Missing required fields: empId, month, base, amount"));
        }

        String employeeId = payrollRequest.get("empId").toString();
        String monthStr = (String) payrollRequest.get("month");
        
        // Log for debugging
        System.out.println("Looking up employee with ID: " + employeeId);
        System.out.println("Processing month: " + monthStr);

        Double base = Double.valueOf(payrollRequest.get("base").toString());
        Double bonus = Double.valueOf(payrollRequest.get("bonus").toString());
        Double deductions = Double.valueOf(payrollRequest.get("deductions").toString());
        Double amount = Double.valueOf(payrollRequest.get("amount").toString());
        String notes = (String) payrollRequest.get("notes");

        // Validate employee exists
        Optional<Employee> employeeOpt = employeeService.getEmployeeById(employeeId);
        if (employeeOpt.isEmpty()) {
            System.out.println("Employee not found with ID: " + employeeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Employee with ID " + employeeId + " not found"));
        }
        System.out.println("Employee found: " + employeeOpt.get().getName());

        // Parse month with better error handling
        YearMonth month;
        try {
            month = YearMonth.parse(monthStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid month format: " + monthStr);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid month format. Expected yyyy-MM format"));
        }

        Optional<Payroll> existingPayroll = payrollService.getPayrollByEmployeeAndMonth(employeeId, month);
        if (existingPayroll.isPresent()) {
            System.out.println("Payroll already exists for employee " + employeeId + " and month " + month);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Payroll already exists for this employee and month"));
        }

        Payroll payroll = new Payroll();
        payroll.setEmployee(employeeOpt.get());
        payroll.setMonth(month);
        payroll.setBase(base);
        // Make sure baseSalary is also set - this is for the database field
        if (payroll.getClass().getDeclaredFields() != null && 
            java.util.Arrays.stream(payroll.getClass().getDeclaredFields())
                .anyMatch(field -> field.getName().equals("baseSalary"))) {
            try {
                java.lang.reflect.Field field = payroll.getClass().getDeclaredField("baseSalary");
                field.setAccessible(true);
                field.set(payroll, base);
            } catch (Exception e) {
                System.out.println("Could not set baseSalary field: " + e.getMessage());
            }
        }
        payroll.setBonus(bonus);
        payroll.setDeductions(deductions);
        payroll.setAmount(amount);
        payroll.setNotes(notes);
        payroll.setCreatedDate(LocalDate.now());

        System.out.println("Saving new payroll: " + payroll);
        Payroll savedPayroll = payrollService.savePayroll(payroll);
        System.out.println("Payroll saved successfully with ID: " + savedPayroll.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPayroll);

    } catch (NumberFormatException e) {
        System.out.println("Number format error: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid number format: " + e.getMessage()));
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Unexpected error creating payroll: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error creating payroll: " + e.getMessage()));
    }
}

    @PutMapping("/{id}")
    public ResponseEntity<Payroll> updatePayroll(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payrollRequest) {
        try {
            Optional<Payroll> existingPayrollOpt = payrollService.getPayrollById(id);
            if (existingPayrollOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Payroll existingPayroll = existingPayrollOpt.get();

            if (payrollRequest.containsKey("base")) {
                existingPayroll.setBase(Double.valueOf(payrollRequest.get("base").toString()));
            }

            if (payrollRequest.containsKey("bonus")) {
                existingPayroll.setBonus(Double.valueOf(payrollRequest.get("bonus").toString()));
            }

            if (payrollRequest.containsKey("deductions")) {
                existingPayroll.setDeductions(Double.valueOf(payrollRequest.get("deductions").toString()));
            }

            if (payrollRequest.containsKey("amount")) {
                existingPayroll.setAmount(Double.valueOf(payrollRequest.get("amount").toString()));
            }

            if (payrollRequest.containsKey("notes")) {
                existingPayroll.setNotes((String) payrollRequest.get("notes"));
            }

            Payroll updatedPayroll = payrollService.savePayroll(existingPayroll);
            return ResponseEntity.ok(updatedPayroll);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayroll(@PathVariable Long id) {
        if (payrollService.getPayrollById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        payrollService.deletePayroll(id);
        return ResponseEntity.noContent().build();
    }
}