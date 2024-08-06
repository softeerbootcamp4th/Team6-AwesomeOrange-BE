package hyundai.softeer.orange.event.fcfs.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisLuaFcfsService implements FcfsService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Integer> numberRedisTemplate;
    private final RedisTemplate<String, Boolean> booleanRedisTemplate;

    @Override
    public boolean participate(Long eventSequence, String userId) {
        String fcfsId = FcfsUtil.keyFormatting(eventSequence.toString());
        if (isEventEnded(fcfsId)) {
            return false;
        }

        // 이미 당첨된 사용자인지 확인
        if(stringRedisTemplate.opsForZSet().rank(FcfsUtil.winnerFormatting(eventSequence.toString()), userId) != null){
            throw new FcfsEventException(ErrorCode.ALREADY_WINNER);
        }

        // 잘못된 이벤트 참여 시간
        String startTime = stringRedisTemplate.opsForValue().get(FcfsUtil.startTimeFormatting(eventSequence.toString()));
        if(startTime == null) {
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }

        if (LocalDateTime.now().isBefore(LocalDateTime.parse(startTime))){
            throw new FcfsEventException(ErrorCode.INVALID_EVENT_TIME);
        }

        long timestamp = System.currentTimeMillis();
        String script = "local count = redis.call('zcard', KEYS[1]) " +
                "if count < tonumber(ARGV[1]) then " +
                "    redis.call('zadd', KEYS[1], ARGV[2], ARGV[3]) " +
                "    return redis.call('zcard', KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end";
        Long result = stringRedisTemplate.execute(
                RedisScript.of(script, Long.class),
                Collections.singletonList(FcfsUtil.winnerFormatting(eventSequence.toString())),
                String.valueOf(numberRedisTemplate.opsForValue().get(fcfsId)),
                String.valueOf(timestamp),
                userId
        );

        if(result == null || result <= 0) {
            endEvent(fcfsId);  // 이벤트 종료 플래그 설정
            return false;
        }

        stringRedisTemplate.opsForZSet().add(FcfsUtil.winnerFormatting(eventSequence.toString()), userId, System.currentTimeMillis());
        log.info("Event Sequence: {}, User ID: {}, Timestamp: {}", eventSequence, userId, timestamp);
        return true;
    }

    private boolean isEventEnded(String fcfsId) {
        return booleanRedisTemplate.opsForValue().get(FcfsUtil.endFlagFormatting(fcfsId)) != null;
    }

    private void endEvent(String fcfsId) {
        booleanRedisTemplate.opsForValue().set(FcfsUtil.endFlagFormatting(fcfsId), true);
    }
}
