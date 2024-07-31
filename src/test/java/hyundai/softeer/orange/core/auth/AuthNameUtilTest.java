package hyundai.softeer.orange.core.auth;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AuthNameUtilTest {

    @DisplayName("문자열을 넘기면 prefix 붙여서 반환")
    @Test
    void testAuthNameString(){
        String defaultName = "Test";
        String expected = AuthNameUtil.AUTH_PREFIX + defaultName;

        String actual = AuthNameUtil.authName(defaultName);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("클래스를 넘기면 클래스 이름에 prefix 붙여서 반환")
    @Test
    void testAuthNameClazz(){
        String expected = AuthNameUtil.AUTH_PREFIX + "AuthNameUtil";

        String actual = AuthNameUtil.authName(AuthNameUtil.class);

        assertThat(actual).isEqualTo(expected);
    }
}