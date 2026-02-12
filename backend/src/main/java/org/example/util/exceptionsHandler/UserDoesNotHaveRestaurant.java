package org.example.util.exceptionsHandler;

public class UserDoesNotHaveRestaurant extends RuntimeException {
    public UserDoesNotHaveRestaurant() {
        super("User doesn't have restaurant");
    }
}
