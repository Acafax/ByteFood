package org.example.dtos.device_auth;

import jakarta.validation.constraints.NotBlank;

public record DeviceApiKeyResetRequest(
        @NotBlank(message = "ApiKey cannot be blank")
        String apiKey
) {
}
