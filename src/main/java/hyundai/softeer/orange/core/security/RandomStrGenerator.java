package hyundai.softeer.orange.core.security;

public class RandomStrGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int idx = 0; idx < length; idx++) {
            int randIdx = (int) (Math.random() * CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randIdx));
        }

        return sb.toString();
    }
}
