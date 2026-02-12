package org.example.security;

import jakarta.persistence.EntityNotFoundException;
import org.example.repositories.UserRepository;
import org.example.repositories.projections.UserProjection;
import org.example.util.exceptionsHandler.UserDoesNotHaveRestaurant;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserProjection userProjection = userRepository.getUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " not found"));

        return new SecurityUser(userProjection);
    }

//    public Boolean userRestaurantExist(Long restaurantId)throws UsernameNotFoundException{
//        return userRepository.userRestaurantExist(restaurantId);
//    }

    public Boolean userExistWithThisEmail(String email)throws UsernameNotFoundException{
        return userRepository.existsUserByEmail(email);
    }

    public SecurityUser getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if(principal instanceof SecurityUser securityUser){
            return securityUser;
        }
        throw new IllegalStateException("User not found in security context");
    }

    public UserRole getCurrentUserRole(){
        return getCurrentUser().getRole();
    }

    public Long getCurrentUserId(){
        return getCurrentUser().getId();
    }

    public Long getCurrentRestaurantId(){
        return getCurrentUser().getRestaurantId()
                .orElseThrow(UserDoesNotHaveRestaurant::new);
    }

    public void checkAccessToResource(Long resourceRestaurantId){
        Long userRestaurantId = getCurrentRestaurantId();
        if(!userRestaurantId.equals(resourceRestaurantId)){
            throw new AccessDeniedException("User does not have access to this resource (id):"+ resourceRestaurantId);
        }
    }


}
