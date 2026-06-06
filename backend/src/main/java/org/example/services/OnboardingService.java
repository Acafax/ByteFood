package org.example.services;

import org.example.dtos.onboarding.CreateRestaurantOnboardingRequest;
import org.example.dtos.onboarding.RestaurantOnboardingResponse;
import org.example.models.Restaurant;
import org.example.models.Stock;
import org.example.models.User;
import org.example.repositories.RestaurantRepository;
import org.example.repositories.StockRepository;
import org.example.repositories.UserRepository;
import org.example.security.CustomUserDetailsService;
import org.example.security.SecurityUser;
import org.example.security.UserRole;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OnboardingService {

    private final CustomUserDetailsService customUserDetailsService;
    private final RestaurantRepository restaurantRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    public OnboardingService(
            CustomUserDetailsService customUserDetailsService,
            RestaurantRepository restaurantRepository,
            StockRepository stockRepository,
            UserRepository userRepository
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.restaurantRepository = restaurantRepository;
        this.stockRepository = stockRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RestaurantOnboardingResponse createRestaurant(CreateRestaurantOnboardingRequest request) {
        SecurityUser currentUser = customUserDetailsService.getCurrentUser();

        if (!UserRole.MANAGER.equals(currentUser.getRole())) {
            throw new AccessDeniedException("Only managers can create a restaurant during onboarding");
        }

        if (currentUser.getRestaurantId().isPresent()) {
            throw new AccessDeniedException("Manager already has a restaurant assigned");
        }

        Stock stock = new Stock();
        stock.setName(request.stockName());
        Stock savedStock = stockRepository.save(stock);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.restaurantName());
        restaurant.setRestaurantStock(savedStock);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        User manager = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        manager.setRestaurant(savedRestaurant);
        userRepository.save(manager);

        return new RestaurantOnboardingResponse(
                savedRestaurant.getId(),
                savedRestaurant.getName(),
                savedStock.getId(),
                savedStock.getName()
        );
    }
}
