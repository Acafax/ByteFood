package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dtos.device_auth.DeviceApiKeyResetRequest;
import org.example.dtos.device_auth.DeviceAuthPayload;
import org.example.services.DeviceAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/device-api-key")
public class DeviceAuthController {

    private final DeviceAuthService deviceAuthService;

    public DeviceAuthController(DeviceAuthService deviceAuthService) {
        this.deviceAuthService = deviceAuthService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateDeviceApiKey(@RequestBody DeviceAuthPayload deviceAuthPayload) {
        String apiKey = deviceAuthService.saveApiKeyInRedis(deviceAuthPayload);
        return ResponseEntity.ok().body(apiKey);
    }

    @PostMapping("/reset")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Void> resetDeviceApiKey(@Valid @RequestBody DeviceApiKeyResetRequest request) {
        deviceAuthService.resetApiKey(request.apiKey());
        return ResponseEntity.noContent().build();
    }
}
