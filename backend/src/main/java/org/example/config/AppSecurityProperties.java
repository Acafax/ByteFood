package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private String corsAllowedOrigins = "http://localhost:5173,http://localhost:8080";

    public List<String> getCorsAllowedOriginsList() {
        return Arrays.stream(corsAllowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
    }

    public String getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(String corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }
}
