package org.example.posFX.session;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  Stores the JWT access token only in process memory (without writing to disk)
 */
public final class AuthSession {

    private final AtomicReference<String> accessToken = new AtomicReference<>();

    public void setAccessToken(String token) {
        accessToken.set(token != null ? token.trim() : null);
    }

    public Optional<String> getAccessToken() {
        return Optional.ofNullable(accessToken.get()).filter(s -> !s.isBlank());
    }

    public void clear() {
        accessToken.set(null);
    }
}
