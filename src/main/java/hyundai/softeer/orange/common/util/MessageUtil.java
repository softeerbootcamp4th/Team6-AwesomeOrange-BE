package hyundai.softeer.orange.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtil {

    public static final String BAD_INPUT = "null 혹은 빈 값이 입력되었습니다.";
    public static final String OUT_OF_SIZE = "정해진 크기를 벗어났습니다.";
    public static final String INVALID_PHONE_NUMBER = "올바르지 않은 전화번호 형식입니다.";
    public static final String INVALID_AUTH_CODE = "인증번호는 숫자로만 입력해주세요.";
}
