package org.example.dtos;

import org.example.models.User;
import org.example.security.UserRole;

public record UserResponseDto(
        Long id,
        String email,
        UserRole role,
        String name,
        String lastName,
        Long restaurantId
) {
    public static UserResponseDto from(User user) {
        Long restaurantId = user.getRestaurant() != null ? user.getRestaurant().getId() : null;
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getName(),
                user.getLastName(),
                restaurantId
        );
    }
}
