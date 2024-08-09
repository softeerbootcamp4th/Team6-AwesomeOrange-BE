package hyundai.softeer.orange.event.fcfs.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@Primary
@Service
public class RedisLuaFcfsService implements FcfsService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Integer> numberRedisTemplate;
    private final RedisTemplate<String, Boolean> booleanRedisTemplate;

    @Override
    public boolean participate(Long eventSequence, String userId) {
        // 이벤트 종료 여부 확인
        if (isEventEnded(eventSequence)) {
            stringRedisTemplate.opsForSet().add(FcfsUtil.participantFormatting(eventSequence.toString()), userId);
            return false;
        }

        // 이미 이 이벤트에 참여했는지 확인
        if(isParticipated(eventSequence, userId)) {
            throw new FcfsEventException(ErrorCode.ALREADY_PARTICIPATED);
        }

        // 잘못된 이벤트 참여 시간
        String startTime = stringRedisTemplate.opsForValue().get(FcfsUtil.startTimeFormatting(eventSequence.toString()));
        if(startTime == null) {
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }

        if (LocalDateTime.now().isBefore(LocalDateTime.parse(startTime))){
            throw new FcfsEventException(ErrorCode.INVALID_EVENT_TIME);
        }

        String script = "local count = redis.call('zcard', KEYS[1]) " +
                "if count < tonumber(ARGV[1]) then " +
                "    redis.call('zadd', KEYS[1], ARGV[2], ARGV[3]) " +
                "    return redis.call('zcard', KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end";
        long timestamp = System.currentTimeMillis();
        Long result = stringRedisTemplate.execute(
                RedisScript.of(script, Long.class),
                Collections.singletonList(FcfsUtil.winnerFormatting(eventSequence.toString())),
                String.valueOf(numberRedisTemplate.opsForValue().get(FcfsUtil.keyFormatting(eventSequence.toString()))),
                String.valueOf(timestamp),
                userId
        );

        if(result == null || result <= 0) {
            log.info("Event Finished: {},", stringRedisTemplate.opsForZSet().zCard(FcfsUtil.winnerFormatting(eventSequence.toString())));
            stringRedisTemplate.opsForSet().add(FcfsUtil.participantFormatting(eventSequence.toString()), userId);
            endEvent(eventSequence);
            return false;
        }

        stringRedisTemplate.opsForZSet().add(FcfsUtil.winnerFormatting(eventSequence.toString()), userId, System.currentTimeMillis());
        stringRedisTemplate.opsForSet().add(FcfsUtil.participantFormatting(eventSequence.toString()), userId);
        log.info("Participating Success: {}, User ID: {}, Timestamp: {}", eventSequence, userId, timestamp);
        return true;
    }

    public boolean isParticipated(Long eventSequence, String userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(FcfsUtil.participantFormatting(eventSequence.toString()), userId));
    }

    private boolean isEventEnded(Long eventSequence) {
        return Boolean.TRUE.equals(booleanRedisTemplate.opsForValue().get(FcfsUtil.endFlagFormatting(eventSequence.toString())));
    }

    private void endEvent(Long eventSequence) {
        booleanRedisTemplate.opsForValue().set(FcfsUtil.endFlagFormatting(eventSequence.toString()), true);
    }
}
