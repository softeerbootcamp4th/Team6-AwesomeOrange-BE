package hyundai.softeer.orange.core.security;

import hyundai.softeer.orange.common.exception.InternalServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
@Component
public class PasswordManagerDefaultImpl implements PasswordManager {
    private static final String SEPARATOR = "@";

    @Override
    public String encrypt(String password) {
        // salt 구하기
        String salt = RandomStrGenerator.generate(30);
            String saltedPassword = getSaltedPassword(password, salt);
            return saltedPassword + SEPARATOR + salt;
    }

    private String getSaltedPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String passwordWithSalt = password + salt;

            md.update(passwordWithSalt.getBytes());
            return Base64.getEncoder().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            throw new InternalServerException();
        }
    }

    @Override
    public boolean verify(String password, String encryptedPassword) {
        String[] beforePasswordInfo = encryptedPassword.split(SEPARATOR);
        if(beforePasswordInfo.length != 2) {
            log.error("서버 측 비밀번호 규격이 호환되지 않습니다. 저장된 비밀번호가 현재 시스템과 일치하는지 확인하세요.");
            throw new InternalServerException();
        }
        String saltedPassword = beforePasswordInfo[0];
        String salt = beforePasswordInfo[1];

        String newSaltedPassword = getSaltedPassword(password, salt);

        return saltedPassword.equals(newSaltedPassword);
    }
}
