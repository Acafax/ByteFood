package org.example.dtos.user;

import org.example.security.UserRole;

public record UserInformation(Long restaurantId, String email, String username, UserRole role) {
}
