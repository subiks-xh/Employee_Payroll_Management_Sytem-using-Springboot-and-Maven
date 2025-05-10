# Employee_Payroll_Management_Sytem-using-Springboot-and-Maven

Project Overview
Payroll Pro is a robust Spring Boot application designed to streamline employee payroll management processes. It offers a comprehensive solution for HR departments and finance teams to efficiently manage employee information and payroll data through a RESTful API interface.
Features

Employee Management: Add, update, view, and delete employee records
Payroll Processing: Generate and manage monthly payroll records for employees
Salary Calculation: Automatic calculation of net salary based on base pay, bonuses, and deductions
Monthly Reports: View payroll data by month or employee
Data Validation: Built-in validation to prevent duplicate payroll entries and ensure data integrity

Technology Stack

Backend: Java 17, Spring Boot 3.x
Database: MySQL/PostgreSQL (configurable)
Build Tool: Maven
API Documentation: SpringDoc OpenAPI (Swagger)
Testing: JUnit 5, Mockito
Lombok: For reducing boilerplate code

Getting Started
Prerequisites

Java 17 or higher
Maven 3.6 or higher
MySQL/PostgreSQL database

Installation

Clone the repository:
bashgit clone https://github.com/yourusername/payroll-pro.git
cd payroll-pro

Configure the database connection in src/main/resources/application.properties:
propertiesspring.datasource.url=jdbc:mysql://localhost:3306/payrolldb
spring.datasource.username=root
spring.datasource.password=your_password

Build the project:
bashmvn clean install

Run the application:
bashmvn spring-boot:run
Alternatively, run the JAR file:
bashjava -jar target/demopayrollpro-0.0.1-SNAPSHOT.jar

Access the API at http://localhost:8080/api/

API Endpoints
Employee Endpoints

GET /api/employees - Get all employees
GET /api/employees/{id} - Get employee by ID
POST /api/employees - Create new employee
PUT /api/employees/{id} - Update employee
DELETE /api/employees/{id} - Delete employee

Payroll Endpoints

GET /api/payrolls - Get all payrolls
GET /api/payrolls/{id} - Get payroll by ID
GET /api/payrolls/employee/{employeeId} - Get payrolls by employee
GET /api/payrolls/month/{month} - Get payrolls by month (format: yyyy-MM)
GET /api/payrolls/employee/{employeeId}/month/{month} - Get payroll by employee and month
POST /api/payrolls - Create new payroll
PUT /api/payrolls/{id} - Update payroll
DELETE /api/payrolls/{id} - Delete payroll
