package org.example.dtos;

import java.time.LocalDateTime;
import java.util.Date;

public record LoginResponse(LocalDateTime time, Date expirationTime, String token) {
}
