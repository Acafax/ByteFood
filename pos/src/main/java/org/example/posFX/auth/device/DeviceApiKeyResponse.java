package org.example.posFX.auth.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeviceApiKeyResponse(String apiKey) {
}
