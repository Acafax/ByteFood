package org.example.util.exceptionsHandler;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String email){
        super(String.format("User with email: %s already exist", email));
    }
}
