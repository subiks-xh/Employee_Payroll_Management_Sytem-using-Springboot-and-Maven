package com.example.demopayrollpro.model;

import java.time.LocalDate;
import java.time.YearMonth;

import com.example.demopayrollpro.converter.YearMonthConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payrolls")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payroll {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;
    
    @Convert(converter = YearMonthConverter.class)
    @Column(nullable = false)
    private YearMonth month;
    
    // Main field used in the entity
    @Column(name = "base") 
    private double base;
    
    // Synchronized field for database compatibility
    @Column(name = "base_salary")
    private double baseSalary;
    
    private double bonus;
    private double deductions;
    private double amount;
    private String notes;
    
    // Changed column name to match database schema
    @Column(name = "created_at", nullable = false)
    private LocalDate createdDate;
    
    // Keep created_date field for backward compatibility (if needed)
    @Column(name = "created_date")
    private LocalDate createdDateLegacy;
    
    // Override the setBase method to ensure both fields are set
    public void setBase(double base) {
        this.base = base;
        this.baseSalary = base;
    }
    
    // Override the setBaseSalary method to ensure both fields are set
    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
        this.base = baseSalary;
    }
    
    // Update createdDate setter to sync both date fields
    public void setCreatedDate(LocalDate date) {
        this.createdDate = date;
        this.createdDateLegacy = date;
    }
}