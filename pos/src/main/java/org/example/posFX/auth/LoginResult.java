package org.example.posFX.auth;

public record LoginResult(boolean success, String errorMessage, String accessToken) {

    public static LoginResult ok(String accessToken) {
        return new LoginResult(true, null, accessToken);
    }

    public static LoginResult fail(String errorMessage) {
        return new LoginResult(false, errorMessage, null);
    }
}
