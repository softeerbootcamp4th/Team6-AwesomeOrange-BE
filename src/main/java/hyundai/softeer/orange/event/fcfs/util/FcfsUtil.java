package hyundai.softeer.orange.event.fcfs.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FcfsUtil {

    public static String keyFormatting(String fcfsId) {
        return fcfsId + ":fcfs";
    }

    public static String startTimeFormatting(String fcfsId) {
        return fcfsId + "_start";
    }

    public static String endFlagFormatting(String fcfsId) {
        return fcfsId + "_end";
    }
}
