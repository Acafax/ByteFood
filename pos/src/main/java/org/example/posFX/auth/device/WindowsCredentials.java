package org.example.posFX.auth.device;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WindowsCredentials implements SystemCredentials {

    private static final Logger LOG = Logger.getLogger(WindowsCredentials.class.getName());

    static final String SERVICE_NAME = "posFX";
    static final String SECRET_KEY = "posDevice:ApiKey";

    private static final int CRED_TYPE_GENERIC = 1;
    private static final int CRED_PERSIST_LOCAL_MACHINE = 2;

    @Override
    public Optional<String> getCredentials() {
        PointerByReference credentialReference = new PointerByReference();

        try {
            boolean success = CredApi.INSTANCE.CredRead(
                    new WString(SECRET_KEY),
                    CRED_TYPE_GENERIC,
                    0,
                    credentialReference
            );

            if (!success) {
                int error = Kernel32.INSTANCE.GetLastError();
                if (error == WinError.ERROR_NOT_FOUND) {
                    return Optional.empty();
                }
                throw new CredentialStorageException(
                        "Nie udało się odczytać ApiKey z Windows Credential Manager (kod błędu: " + error + ")."
                );
            }

            CREDENTIAL credential = new CREDENTIAL(credentialReference.getValue());
            byte[] secretBytes = credential.CredentialBlob.getByteArray(0, credential.CredentialBlobSize);
            return Optional.of(new String(secretBytes, StandardCharsets.UTF_8));
        } catch (CredentialStorageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CredentialStorageException("Nie udało się odczytać ApiKey z Windows Credential Manager.", ex);
        } finally {
            if (credentialReference.getValue() != null) {
                CredApi.INSTANCE.CredFree(credentialReference.getValue());
            }
        }
    }

    @Override
    public void setCredentials(String key) {
        validateKey(key);

        byte[] secretBytes = key.getBytes(StandardCharsets.UTF_8);
        MemoryGuard secretMemory = new MemoryGuard(secretBytes);

        CREDENTIAL credential = new CREDENTIAL();
        credential.Type = CRED_TYPE_GENERIC;
        credential.TargetName = SECRET_KEY;
        credential.Comment = SERVICE_NAME;
        credential.CredentialBlobSize = secretBytes.length;
        credential.CredentialBlob = secretMemory.pointer();
        credential.Persist = CRED_PERSIST_LOCAL_MACHINE;
        credential.AttributeCount = 0;
        credential.Attributes = null;
        credential.TargetAlias = null;
        credential.UserName = SERVICE_NAME;
        credential.write();

        try {
            if (!CredApi.INSTANCE.CredWrite(credential, 0)) {
                int error = Kernel32.INSTANCE.GetLastError();
                throw new CredentialStorageException(
                        "Nie udało się zapisać ApiKey w Windows Credential Manager (kod błędu: " + error + ")."
                );
            }
            LOG.log(Level.FINE, "Zapisano ApiKey w Windows Credential Manager dla targetName={0}", SECRET_KEY);
        } catch (CredentialStorageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CredentialStorageException("Nie udało się zapisać ApiKey w Windows Credential Manager.", ex);
        } finally {
            secretMemory.close();
        }
    }

    @Override
    public void deleteCredentials() {
        try {
            if (CredApi.INSTANCE.CredDelete(new WString(SECRET_KEY), CRED_TYPE_GENERIC, 0)) {
                LOG.log(Level.FINE, "Usunięto ApiKey z Windows Credential Manager dla targetName={0}", SECRET_KEY);
                return;
            }

            int error = Kernel32.INSTANCE.GetLastError();
            if (error == WinError.ERROR_NOT_FOUND) {
                return;
            }

            throw new CredentialStorageException(
                    "Nie udało się usunąć ApiKey z Windows Credential Manager (kod błędu: " + error + ")."
            );
        } catch (CredentialStorageException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new CredentialStorageException("Nie udało się usunąć ApiKey z Windows Credential Manager.", ex);
        }
    }

    private static void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new CredentialStorageException("ApiKey nie może być pusty.");
        }
    }

    private interface CredApi extends StdCallLibrary {

        CredApi INSTANCE = Native.load("advapi32", CredApi.class, W32APIOptions.UNICODE_OPTIONS);

        boolean CredWrite(CREDENTIAL credential, int flags);

        boolean CredRead(WString targetName, int type, int reserved, PointerByReference credential);

        boolean CredDelete(WString targetName, int type, int reserved);

        void CredFree(Pointer buffer);
    }

    @Structure.FieldOrder({
            "Flags",
            "Type",
            "TargetName",
            "Comment",
            "LastWritten",
            "CredentialBlobSize",
            "CredentialBlob",
            "Persist",
            "AttributeCount",
            "Attributes",
            "TargetAlias",
            "UserName"
    })
    static class CREDENTIAL extends Structure {

        public int Flags;
        public int Type;
        public String TargetName;
        public String Comment;
        public WinBase.FILETIME LastWritten;
        public int CredentialBlobSize;
        public Pointer CredentialBlob;
        public int Persist;
        public int AttributeCount;
        public Pointer Attributes;
        public String TargetAlias;
        public String UserName;

        CREDENTIAL() {
        }

        CREDENTIAL(Pointer pointer) {
            super(pointer);
            read();
        }
    }

    private static final class MemoryGuard implements AutoCloseable {

        private final Pointer pointer;
        private final int size;

        private MemoryGuard(byte[] bytes) {
            this.size = bytes.length;
            this.pointer = new Memory(bytes);
        }

        private Pointer pointer() {
            return pointer;
        }

        @Override
        public void close() {
            if (pointer != null) {
                pointer.clear(size);
            }
        }

        private static final class Memory extends com.sun.jna.Memory {

            private Memory(byte[] data) {
                super(data.length);
                write(0, data, 0, data.length);
            }
        }
    }
}
