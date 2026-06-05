package org.example.dtos.user;

import java.time.LocalDateTime;
import java.util.Date;

public record LoginResponse(LocalDateTime time, Date expirationTime, String token) {
}
