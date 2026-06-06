package org.example.dtos.employee;

import org.example.security.UserRole;

public record EmployeeResponse(
        Long id,
        String email,
        String name,
        String lastName,
        UserRole role
) {
}
