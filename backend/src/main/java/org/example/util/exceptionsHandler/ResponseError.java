package org.example.util.exceptionsHandler;

import java.time.LocalDateTime;

public record ResponseError(int status, String error, String message, LocalDateTime timestamp){
}
