package com.example


data class Employee(
    val name: String,
    val email: String?
)

data class EmployeeResponse(
    val employees: List<Employee>
)