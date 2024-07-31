package hyundai.softeer.orange.security;

import org.springframework.stereotype.Component;

public interface PasswordManager {
    String encrypt(String password);
    boolean verify(String password, String encryptedPassword);
}
