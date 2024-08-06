package hyundai.softeer.orange.event.fcfs.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
@Primary
public class RedisLockFcfsService implements FcfsService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Boolean> booleanRedisTemplate;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Integer> numberRedisTemplate;

    @Override
    public boolean participate(Long eventSequence, String userId) {
        String fcfsId = FcfsUtil.keyFormatting(eventSequence.toString());
        // 불필요한 Lock 접근을 막기 위한 종료 flag 확인
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

        // Lock을 이용한 참여 처리
        final RLock lock = redissonClient.getLock("LOCK:" + fcfsId);
        try {
            boolean usingLock = lock.tryLock(1L, 3L, TimeUnit.SECONDS);
            if (!usingLock) {
                return false;
            }

            final long quantity = availableCoupons(fcfsId);
            if (quantity <= 0) {
                endEvent(fcfsId);  // 이벤트 종료 플래그 설정
                return false;
            }

            numberRedisTemplate.opsForValue().decrement(fcfsId);
            stringRedisTemplate.opsForZSet().add(FcfsUtil.winnerFormatting(eventSequence.toString()), userId, System.currentTimeMillis());
            log.info("{} - 이벤트 참여 성공, 잔여 쿠폰: {}", userId, availableCoupons(fcfsId));
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private Integer availableCoupons(String key) {
        return numberRedisTemplate.opsForValue().get(key);
    }

    private boolean isEventEnded(String fcfsId) {
        Boolean endFlag = booleanRedisTemplate.opsForValue().get(FcfsUtil.endFlagFormatting(fcfsId));
        return Boolean.TRUE.equals(endFlag);
    }

    private void endEvent(String fcfsId) {
        booleanRedisTemplate.opsForValue().set(FcfsUtil.endFlagFormatting(fcfsId), true);
    }
}
