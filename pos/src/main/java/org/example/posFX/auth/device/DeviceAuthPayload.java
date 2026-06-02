package org.example.posFX.auth.device;

public record DeviceAuthPayload(
        String email,
        String password,
        String posName,
        String restaurantName
) {
}
