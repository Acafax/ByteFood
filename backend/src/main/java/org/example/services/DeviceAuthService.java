package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.dtos.LoginRequest;
import org.example.dtos.LoginResponse;
import org.example.dtos.device_auth.DeviceAuthPayload;
import org.example.dtos.device_auth.DeviceAuthResult;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.google.common.hash.Hashing;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class DeviceAuthService {


    private final StringRedisTemplate stringRedisTemplate;
    private final AuthService authService;

    public DeviceAuthService(StringRedisTemplate stringRedisTemplate, AuthService authService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.authService = authService;
    }

    public String saveApiKeyInRedis(DeviceAuthPayload deviceAuthPayload){
        var apiKey =  generateApiKey(deviceAuthPayload);

        try {

            stringRedisTemplate.opsForValue().setIfAbsent(apiKey, "1");

        }catch (IllegalStateException e){
            throw new KeyAlreadyExistsException("Key already Exist, Key: " + apiKey);
        }
        return apiKey;
    }

    public void resetApiKey(String apiKey){

        String deletedApiKey = stringRedisTemplate.opsForValue().getAndDelete(apiKey);


    }

    private String generateApiKey(DeviceAuthPayload deviceAuthPayload){
        DeviceAuthResult deviceAuthResult = authService.validateManagerForDeviceAuthentication(deviceAuthPayload);

        String combinedStrings =
                deviceAuthPayload.restaurantName() + "|"
                        + deviceAuthPayload.posName() +"|"
                        + deviceAuthResult.restaurantId();

        return Hashing.sha256()
                .hashString(combinedStrings, StandardCharsets.UTF_8)
                .toString();
    }







}
