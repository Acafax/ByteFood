package org.example.repositories.projections;

import org.example.security.UserRole;

public interface UserProjection {
    Long getId();
    String getEmail();
    String getPassword();
    UserRole getRole();
    String getName();
    String getLastName();
    Long getRestaurantId();
}

