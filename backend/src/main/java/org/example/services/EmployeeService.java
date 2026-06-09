package org.example.services;

import org.example.dtos.employee.CreateEmployeeRequest;
import org.example.dtos.employee.EmployeeResponse;
import org.example.models.Restaurant;
import org.example.models.User;
import org.example.repositories.RestaurantRepository;
import org.example.repositories.UserRepository;
import org.example.security.CustomUserDetailsService;
import org.example.security.SecurityUser;
import org.example.security.UserRole;
import org.example.util.exceptionsHandler.UserAlreadyExistException;
import org.example.util.exceptionsHandler.UserDoesNotHaveRestaurant;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final CustomUserDetailsService customUserDetailsService;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(
            CustomUserDetailsService customUserDetailsService,
            RestaurantRepository restaurantRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        SecurityUser currentUser = customUserDetailsService.getCurrentUser();

        currentUser.verifyIfUserIsManager();

        Long restaurantId = currentUser.getRestaurantId()
                .orElseThrow(UserDoesNotHaveRestaurant::new);

        if (customUserDetailsService.userExistWithThisEmail(request.email())) {
            throw new UserAlreadyExistException(request.email());
        }

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalStateException("Restaurant not found"));

        String encodedPassword = passwordEncoder.encode(request.password());
        User employee = new User(
                request.email(),
                encodedPassword,
                UserRole.EMPLOYEE,
                request.name(),
                request.lastName()
        );
        employee.setRestaurant(restaurant);
        User savedEmployee = userRepository.save(employee);

        return new EmployeeResponse(
                savedEmployee.getId(),
                savedEmployee.getEmail(),
                savedEmployee.getName(),
                savedEmployee.getLastName(),
                savedEmployee.getRole()
        );
    }
}
