package hyundai.softeer.orange.core.security;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordManagerDefaultImplTest {
    PasswordManagerDefaultImpl pm = new PasswordManagerDefaultImpl();

    @DisplayName("생성한 비밀번호는 password@salt 형식을 띄며, 정상적인 비밀번호는 해독된다.")
    @Test
    void passwordMatchCase() {
        String password = "test";
        String password2 = "test";
        String encryptedPassword = pm.encrypt(password);
        String[] passwordAndSalt = encryptedPassword.split("@");

        boolean isPasswordMatch = pm.verify(password2, encryptedPassword);
        assertThat(isPasswordMatch).isEqualTo(true);
    }

    @DisplayName("잘못된 비밀번호를 넣으면 해독 안됨")
    @Test
    void verify_returnNotValidIfPasswordNotMatch() {
        String password = "test";
        String password2 = "invalid";
        String encryptedPassword = pm.encrypt(password);

        boolean isPasswordMatch = pm.verify(password2, encryptedPassword);

        assertThat(isPasswordMatch).isEqualTo(false);
    }
}