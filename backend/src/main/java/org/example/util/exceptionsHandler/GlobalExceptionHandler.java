package org.example.util.exceptionsHandler;


import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.*;
import org.springframework.web.client.HttpServerErrorException.*;

import javax.security.auth.login.CredentialException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseError> handlerJsonError(HttpMessageNotReadableException ex) {
        // Sprawdzenie, czy błąd wynika z nieznanego pola w JSON
        if (ex.getCause() instanceof UnrecognizedPropertyException unrecognizedEx) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseError(
                            HttpStatus.BAD_REQUEST.value(),
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            "Unrecognized field: " + unrecognizedEx.getPropertyName(),
                            LocalDateTime.now()
                    ));
        }
        // np. błędy składniowe, brak nawiasu itp.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Malformed JSON request",
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handlerNotFoundException(NotFound ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handleEntityNotFound(EntityNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(
                        HttpStatus.NOT_FOUND.value(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }


    @ExceptionHandler
    ResponseEntity<ResponseError> handlerBadRequestException(BadRequest ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handlerInternalServerException(InternalServerError ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseError(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handlerForbiddenException(Forbidden ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseError(
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handlerAccessDeniedExceptionException(AccessDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseError(
                        HttpStatus.FORBIDDEN.value(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handlerCredentialException(CredentialException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handlerUnauthorizedException(Unauthorized ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseError(
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handlerConflictException(EntityExistsException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseError(
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handlerConflictException(UserAlreadyExistException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseError(
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler
    ResponseEntity<ResponseError> handlerConflictException(UserDoesNotHaveRestaurant ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseError(
                        HttpStatus.CONFLICT.value(),
                        HttpStatus.CONFLICT.getReasonPhrase(),
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }



}
