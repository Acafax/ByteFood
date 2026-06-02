package org.example.posFX.auth.device;

import org.freedesktop.dbus.DBusPath;
import org.freedesktop.dbus.types.Variant;
import org.purejava.secret.api.Collection;
import org.purejava.secret.api.DBusMessageHandler;
import org.purejava.secret.api.EncryptedSession;
import org.purejava.secret.api.Item;
import org.purejava.secret.api.Secret;
import org.purejava.secret.api.Service;
import org.purejava.secret.api.Util;
import org.purejava.secret.api.errors.SecretNoSuchObjectException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LinuxCredentials implements SystemCredentials {

    static final String SERVICE_NAME = "posFX";
    static final String SECRET_KEY = "posDevice:ApiKey";

    private static final String ATTR_SERVICE = "service";
    private static final String ATTR_ACCOUNT = "account";
    private static final DBusPath NO_PROMPT = new DBusPath("/");

    private final Service service;
    private final EncryptedSession encryptedSession;
    private final Collection defaultCollection;

    public LinuxCredentials() {
        this.service = new Service();
        if (!service.isAvailable()) {
            throw new CredentialStorageException("Secret Service (GNOME Keyring / KWallet) jest niedostępny.");
        }

        this.encryptedSession = new EncryptedSession(service);
        if (!encryptedSession.setupEncryptedSession()) {
            throw new CredentialStorageException("Nie udało się nawiązać sesji Secret Service.");
        }

        DBusMessageHandler.DBusResult<DBusPath> aliasResult = service.readAlias("default");
        if (!aliasResult.isSuccess()) {
            throw new CredentialStorageException(
                    "Nie udało się odczytać domyślnej kolekcji Secret Service.",
                    aliasResult.error()
            );
        }

        this.defaultCollection = new Collection(aliasResult.value());
        service.ensureUnlocked(aliasResult.value());
    }

    @Override
    public Optional<String> getCredentials() {
        try {
            return findItemPath()
                    .flatMap(this::readSecretFromItem);
        } catch (CredentialStorageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CredentialStorageException("Nie udało się odczytać ApiKey z Secret Service.", ex);
        }
    }

    @Override
    public void setCredentials(String key) {
        validateKey(key);

        try {
            Secret secret = encryptedSession.encrypt(key);
            Map<String, Variant<?>> properties = Item.createProperties(SECRET_KEY, lookupAttributes());

            DBusMessageHandler.DBusResult<org.purejava.secret.api.Pair<DBusPath, DBusPath>> createResult =
                    defaultCollection.createItem(properties, secret, true);

            if (createResult == null || !createResult.isSuccess()) {
                throw new CredentialStorageException(
                        "Nie udało się zapisać ApiKey w Secret Service.",
                        createResult != null ? createResult.error() : null
                );
            }

            resolvePrompt(createResult.value().b);
        } catch (CredentialStorageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CredentialStorageException("Nie udało się zapisać ApiKey w Secret Service.", ex);
        }
    }

    @Override
    public void deleteCredentials() {
        try {
            Optional<DBusPath> itemPath = findItemPath();
            if (itemPath.isEmpty()) {
                return;
            }

            Item item = new Item(itemPath.get());
            DBusMessageHandler.DBusResult<DBusPath> deleteResult = item.delete();
            if (!deleteResult.isSuccess()) {
                throw new CredentialStorageException(
                        "Nie udało się usunąć ApiKey z Secret Service.",
                        deleteResult.error()
                );
            }

            resolvePrompt(deleteResult.value());
        } catch (CredentialStorageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CredentialStorageException("Nie udało się usunąć ApiKey z Secret Service.", ex);
        }
    }

    private Optional<DBusPath> findItemPath() {
        DBusMessageHandler.DBusResult<List<DBusPath>> searchResult =
                defaultCollection.searchItems(lookupAttributes());

        if (!searchResult.isSuccess()) {
            if (searchResult.error() instanceof SecretNoSuchObjectException) {
                return Optional.empty();
            }
            throw new CredentialStorageException(
                    "Nie udało się wyszukać ApiKey w Secret Service.",
                    searchResult.error()
            );
        }

        List<DBusPath> items = searchResult.value();
        if (items == null || items.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(items.get(0));
    }

    private Optional<String> readSecretFromItem(DBusPath itemPath) {
        Item item = new Item(itemPath);
        Secret secret = item.getSecret(encryptedSession.getSession());
        if (secret == null) {
            return Optional.empty();
        }

        try {
            char[] value = encryptedSession.decrypt(secret);
            try {
                return Optional.of(new String(value));
            } finally {
                Arrays.fill(value, '\0');
            }
        } catch (Exception ex) {
            throw new CredentialStorageException("Nie udało się odszyfrować ApiKey z Secret Service.", ex);
        }
    }

    private void resolvePrompt(DBusPath promptPath) {
        if (promptPath == null || NO_PROMPT.equals(promptPath)) {
            return;
        }

        DBusPath result = Util.promptAndGetResultAsDBusPath(promptPath);
        if (result == null || NO_PROMPT.equals(result)) {
            throw new CredentialStorageException("Operacja Secret Service wymagała potwierdzenia użytkownika.");
        }
    }

    private static Map<String, String> lookupAttributes() {
        return Map.of(
                ATTR_SERVICE, SERVICE_NAME,
                ATTR_ACCOUNT, SECRET_KEY
        );
    }

    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new CredentialStorageException("ApiKey nie może być pusty.");
        }
    }
}
