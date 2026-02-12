package org.example.dtos;

public record RegisterRequest(String email, String password, String name, String lastName) {
}
