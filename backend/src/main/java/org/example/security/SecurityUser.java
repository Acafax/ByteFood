package org.example.security;

import org.example.repositories.projections.UserProjection;
import org.example.util.exceptionsHandler.UserDoesNotHaveRestaurant;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;


public class SecurityUser implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final UserRole role;
    private final String name;
    private final String lastName;
    private final Optional<Long> restaurantId;

    // Konstruktor dla produkcji - z UserProjection
    public SecurityUser(UserProjection projection) {
        this.id = projection.getId();
        this.email = projection.getEmail();
        this.password = projection.getPassword();
        this.role = projection.getRole();
        this.name = projection.getName();
        this.lastName = projection.getLastName();
        this.restaurantId = Optional.ofNullable(projection.getRestaurantId());
    }

    // Konstruktor dla testów
    public SecurityUser(Long id, String email, String password, UserRole role, String name, String lastName, Long restaurantId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
        this.lastName = lastName;
        this.restaurantId = Optional.ofNullable(restaurantId);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Optional<Long> getRestaurantId() {
        return restaurantId;
    }

    public void verifyIfUserHasRestaurant() {
        if (this.getRestaurantId().isEmpty()) {
            throw new UserDoesNotHaveRestaurant();
        }
    }

    public void verifyIfUserIsManager() {
        if(!UserRole.MANAGER.equals(this.getRole())){
            throw new AccessDeniedException("Only managers can create employees");
        }
    }


}
