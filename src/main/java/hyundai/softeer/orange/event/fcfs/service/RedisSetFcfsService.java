package hyundai.softeer.orange.event.fcfs.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSetFcfsService implements FcfsService {

    private final RedisTemplate<String, Boolean> booleanRedisTemplate;
    private final RedisTemplate<String, Integer> numberRedisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean participate(Long eventSequence, String userId) {
        String key = eventSequence.toString();
        // 이벤트 종료 여부 확인
        if(isEventEnded(key)) {
            stringRedisTemplate.opsForSet().add(FcfsUtil.participantFormatting(key), userId);
            return false;
        }

        // 이미 이 이벤트에 참여했는지 확인
        if(isParticipated(key, userId)) {
            throw new FcfsEventException(ErrorCode.ALREADY_PARTICIPATED);
        }

        // 잘못된 이벤트 참여 시간
        String startTime = stringRedisTemplate.opsForValue().get(FcfsUtil.startTimeFormatting(key));
        if(startTime == null) {
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }
        if (LocalDateTime.now().isBefore(LocalDateTime.parse(startTime))){
            throw new FcfsEventException(ErrorCode.INVALID_EVENT_TIME);
        }

        // 이벤트 인원 마감 여부 확인
        if (isEventFull(key)) {
            log.info("Event Finished: {},", stringRedisTemplate.opsForZSet().zCard(FcfsUtil.winnerFormatting(key)));
            stringRedisTemplate.opsForSet().add(FcfsUtil.participantFormatting(key), userId);
            return false;
        }

        stringRedisTemplate.opsForZSet().add(FcfsUtil.winnerFormatting(key), userId, System.currentTimeMillis());
        stringRedisTemplate.opsForSet().add(FcfsUtil.participantFormatting(key), userId);
        log.info("Participating Success: {}, User ID: {}", eventSequence, userId);
        return true;
    }

    private boolean isParticipated(String key, String userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(FcfsUtil.participantFormatting(key), userId));
    }

    // 이미 종료된 이벤트인지 확인
    private boolean isEventEnded(String key) {
        return Boolean.TRUE.equals(booleanRedisTemplate.opsForValue().get(FcfsUtil.endFlagFormatting(key)));
    }

    // 인원수 마감 여부를 확인하며, synchronized를 통해 동시성 제어
    private synchronized boolean isEventFull(String key) {
        if(isEventEnded(key)){
            return true;
        }

        Long nowCount = stringRedisTemplate.opsForZSet().size(FcfsUtil.winnerFormatting(key));
        if(nowCount == null){
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }
        Integer maxNumber = numberRedisTemplate.opsForValue().get(FcfsUtil.keyFormatting(key));
        if (maxNumber == null) {
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }

        if(nowCount >= maxNumber){
            booleanRedisTemplate.opsForValue().set(FcfsUtil.endFlagFormatting(key), true);
            return true;
        }
        return false;
    }
}
