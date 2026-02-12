package org.example.security;


import lombok.extern.slf4j.Slf4j;
import org.example.repositories.UserRepository;
import org.example.util.exceptionsHandler.UserDoesNotHaveRestaurant;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

import java.util.Optional;

@Slf4j
@Service
public class SecurityService {

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityService(UserRepository userRepository, CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.customUserDetailsService = customUserDetailsService;
    }


    @PreAuthorize("authentication.principal.restaurantId != null")
    public boolean isManager(){
        customUserDetailsService.getCurrentRestaurantId();
        try {
            UserRole currentUserRole = customUserDetailsService.getCurrentUserRole();
            return currentUserRole == UserRole.MANAGER || currentUserRole == UserRole.ADMIN;

        } catch (Exception e) {
            return false;
        }

    }

    public boolean isEmployee(){
        customUserDetailsService.getCurrentRestaurantId();
        UserRole currentUserRole = customUserDetailsService.getCurrentUserRole();
        return currentUserRole == UserRole.EMPLOYEE || currentUserRole ==UserRole.MANAGER || currentUserRole == UserRole.ADMIN;
    }

    public void isItRestaurantObject(Long restaurantIdOfObject) throws HttpClientErrorException.Forbidden {
        Long currentRestaurantId = customUserDetailsService.getCurrentRestaurantId();
        if(!restaurantIdOfObject.equals(currentRestaurantId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied: Object does not belong to your restaurant");
        }
    }

    @Transactional(readOnly = true)
    public boolean hasAccess(Long entityId, JpaRepository<?, Long> repository) {
        Long managerRestaurantId;
        try {
            managerRestaurantId = customUserDetailsService.getCurrentRestaurantId();
        } catch (Exception e) {
            return false;
        }

        Optional<?> entityOpt = repository.findById(entityId);

        if (entityOpt.isEmpty()) {
            return true;
        }

        Object entity = entityOpt.get();

        try {
            Method getRestaurantIdMethod = entity.getClass().getMethod("getRestaurantId");
            Object result = getRestaurantIdMethod.invoke(entity);

            if (result == null) {
                return false;
            }

            Long entityRestaurantId;

            if (result instanceof Long ) {
                entityRestaurantId = (Long) result;
            }else {
                Method getIdMethod = result.getClass().getMethod("getId");
                entityRestaurantId = (Long) getIdMethod.invoke(result);
            }
            return Objects.equals(entityRestaurantId, managerRestaurantId);
        } catch (NoSuchMethodException e) {
            log.error("Encja {} nie posiada metody getRestaurantId() - sprawdzanie uprawnień niemożliwe", entity.getClass().getSimpleName());
            return false;
        } catch (Exception e) {
            log.error("Błąd podczas sprawdzania uprawnień: {}", e.getMessage());
            return false;
        }
    }

}
