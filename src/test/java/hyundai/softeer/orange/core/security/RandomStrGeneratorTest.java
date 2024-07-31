package hyundai.softeer.orange.core.security;

import hyundai.softeer.orange.core.security.RandomStrGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomStrGeneratorTest {
    @DisplayName("generate는 지정된 길이의 랜덤 문자열을 생성한다.")
    @Test
    void testRandomStr() {
        int length = 20;

        String randStr = RandomStrGenerator.generate(length);
        Assertions.assertThat(randStr).isNotBlank().hasSize(length);
        Assertions.assertThat(randStr).matches("[a-zA-Z0-9]{" + length + "}");
    }
}