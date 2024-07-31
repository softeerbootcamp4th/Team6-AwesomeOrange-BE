package hyundai.softeer.orange.common.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantUtil {

    public static final String COMMENTS_KEY = "'comments'";
    public static final String CLIENT_ID = "X-NCP-APIGW-API-KEY-ID";
    public static final String CLIENT_SECRET = "X-NCP-APIGW-API-KEY";// 2시간
    public static final String PHONE_NUMBER_REGEX = "010\\d{8}"; // 010 + 8자리 숫자
    public static final String AUTH_CODE_REGEX = "\\d{6}"; // 6자리 숫자

    public static final double LIMIT_NEGATIVE_CONFIDENCE = 99.5;
    public static final int COMMENTS_SIZE = 20;
    public static final int SCHEDULED_TIME = 1000 * 60 * 60 * 2;
}
