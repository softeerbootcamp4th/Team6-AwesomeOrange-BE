package hyundai.softeer.orange.event.fcfs.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FcfsUtil {

    // 선착순 이벤트 tag
    public static String keyFormatting(String key) {
        return key + ":fcfs";
    }

    // 선착순 이벤트 시작 시각 tag
    public static String startTimeFormatting(String key) {
        return key + "_start";
    }

    // 선착순 이벤트 마감 여부 tag
    public static String endFlagFormatting(String key) {
        return key + "_end";
    }

    // 선착순 이벤트 당첨자 tag
    public static String winnerFormatting(String key) {
        return key + "_winner";
    }

    // 선착순 이벤트 참여자 tag
    public static String participantFormatting(String key) {
        return key + "_participant";
    }

    // 선착순 이벤트 정답 tag
    public static String answerFormatting(String key) {
        return key + ":answer";
    }
}
