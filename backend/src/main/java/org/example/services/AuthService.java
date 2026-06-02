package org.example.services;

import lombok.extern.slf4j.Slf4j;
import org.example.dtos.RegisterRequest;
import org.example.dtos.device_auth.DeviceAuthPayload;
import org.example.dtos.device_auth.DeviceAuthResult;
import org.example.models.User;
import org.example.repositories.UserRepository;
import org.example.security.SecurityUser;
import org.example.security.UserRole;
import org.example.util.exceptionsHandler.UserAlreadyExistException;
import org.example.util.exceptionsHandler.UserDoesNotHaveRestaurant;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.example.dtos.LoginRequest;
import org.example.dtos.LoginResponse;
import org.example.security.CustomUserDetailsService;
import org.example.security.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.Date;


@Slf4j
@Service
public class AuthService {


    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(JwtService jwtService, CustomUserDetailsService customUserDetailsService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest loginRequest){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.email());
        if(!passwordEncoder.matches(loginRequest.password(), userDetails.getPassword())){
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(userDetails);

        Date expirationTime = jwtService.getExpirationTime(token);
        return new LoginResponse(LocalDateTime.now(), expirationTime, token);
    }

    public DeviceAuthResult validateManagerForDeviceAuthentication(DeviceAuthPayload deviceAuthPayload){
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(deviceAuthPayload.email());
        if(!passwordEncoder.matches(deviceAuthPayload.password(), userDetails.getPassword())){
            throw new BadCredentialsException("Invalid credentials");
        }

        SecurityUser user = (SecurityUser) userDetails;
        UserRole role = user.getRole();
        Long restaurantId = user.getRestaurantId().orElseThrow(UserDoesNotHaveRestaurant::new);

        if (role.equals(UserRole.EMPLOYEE)){
            throw new AccessDeniedException("Employee can't authenticate device");
        }

        return new DeviceAuthResult(restaurantId);
    }

    public LoginResponse register(RegisterRequest registerRequest) {
        if (customUserDetailsService.userExistWithThisEmail(registerRequest.email())) throw new UserAlreadyExistException(registerRequest.email());

        UserDetails user = createUser(registerRequest);

        String token = jwtService.generateToken(user);

        Date expirationTime = jwtService.getExpirationTime(token);
        return new LoginResponse(LocalDateTime.now(), expirationTime, token);
    }

    private UserDetails createUser(RegisterRequest registerRequest){
        String encodedPassword = passwordEncoder.encode(registerRequest.password());
        User user = new User(
                registerRequest.email(),
                encodedPassword,
                UserRole.EMPLOYEE,
                registerRequest.name(),
                registerRequest.lastName()
        );

        User createdUser = userRepository.save(user);

        return new SecurityUser(
                createdUser.getId(),
                createdUser.getEmail(),
                createdUser.getPassword(),
                createdUser.getRole(),
                createdUser.getName(),
                createdUser.getLastName(),
                createdUser.getRestaurant() != null ? createdUser.getRestaurant().getId() : null
        );

    }
}
