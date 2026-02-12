package org.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.util.exceptionsHandler.ResponseError;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomSecurityExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    // If the user is authenticated but does not have the right to access the resource or does not have a restaurant assigned
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            if (user.getRestaurantId().isEmpty()){
                createResponse(response, 409, "Conflict", "Manager does not have a restaurant assigned. Please complete onboarding.");
                return;
            }

        }
        createResponse(response, 403, "Forbidden", "You do not have permission to access this resource.");
    }

    // If the user is not authenticated
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            createResponse(response, 401, "Unauthorized",
                    "Authentication required. Please provide a valid token.");
        } else {
            createResponse(response, 401, "Unauthorized",
                    "Invalid or expired token. Please log in again.");
        }
    }

    // helper DRY
    private void createResponse(HttpServletResponse response, int status, String errorTitle, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResponseError responseError = new ResponseError(
                status,
                errorTitle,
                message,
                LocalDateTime.now());
        ;
        objectMapper.writeValue(response.getWriter(), responseError);
    }

}
