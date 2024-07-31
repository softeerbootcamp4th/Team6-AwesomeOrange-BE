package hyundai.softeer.orange.core.security;

public interface PasswordManager {
    String encrypt(String password);
    boolean verify(String password, String encryptedPassword);
}
