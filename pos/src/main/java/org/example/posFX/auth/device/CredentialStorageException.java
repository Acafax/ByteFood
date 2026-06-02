package org.example.posFX.auth.device;

public class CredentialStorageException extends RuntimeException {

    public CredentialStorageException(String message) {
        super(message);
    }

    public CredentialStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
