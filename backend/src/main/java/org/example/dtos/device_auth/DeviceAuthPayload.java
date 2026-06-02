package org.example.dtos.device_auth;

public record DeviceAuthPayload(
        String email,
        String password,
        String posName,
        String restaurantName
) {
}
