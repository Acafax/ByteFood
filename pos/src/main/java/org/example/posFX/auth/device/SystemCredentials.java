package org.example.posFX.auth.device;

import java.util.Optional;

public interface SystemCredentials {

    Optional<String> getCredentials();

    void setCredentials(String key);

    void deleteCredentials();

}
