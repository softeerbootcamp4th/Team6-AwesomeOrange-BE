package hyundai.softeer.orange.core.auth;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AuthNameUtil {
    public static final String AUTH_PREFIX = "AUTH_";
    public static String authName(String name) {
        return AUTH_PREFIX + name;
    }
    public static String authName(Class<?> clazz) {
        return AUTH_PREFIX + clazz.getSimpleName();
    }
}
