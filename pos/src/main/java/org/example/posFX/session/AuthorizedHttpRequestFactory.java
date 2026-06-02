package org.example.posFX.session;

import java.net.URI;
import java.net.http.HttpRequest;


public final class AuthorizedHttpRequestFactory {

    private final AuthSession authSession;

    public AuthorizedHttpRequestFactory(AuthSession authSession) {
        this.authSession = authSession;
    }

 
    public HttpRequest.Builder newBuilder(URI uri) {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(uri);
        authSession.getAccessToken().ifPresent(token ->
                builder.header("Authorization", "Bearer " + token));
        return builder;
    }
}
