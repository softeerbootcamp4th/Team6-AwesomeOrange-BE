package hyundai.softeer.orange.common.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantUtil {

    public static final String COMMENTS_KEY = "'comments'";
    public static final String CLIENT_ID = "X-NCP-APIGW-API-KEY-ID";
    public static final String CLIENT_SECRET = "X-NCP-APIGW-API-KEY";
    public static final double LIMIT_NEGATIVE_CONFIDENCE = 99.5;
}
