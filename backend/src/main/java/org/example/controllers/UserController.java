package org.example.controllers;


import org.example.models.User;
import org.example.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {



    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<User>> getAllUsers(){
        List<User> all = userRepository.getAllUsers();
        return ResponseEntity.ok().body(all);
    }


}
