package hyundai.softeer.orange.event.fcfs.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisLockFcfsService implements FcfsService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

    @Override
    public boolean participate(Long eventSequence, String userId) {
        // time check
        LocalDateTime startTime = (LocalDateTime) redissonClient.getBucket(FcfsUtil.startTimeFormatting(eventSequence.toString())).get();
        if (LocalDateTime.now().isBefore(startTime)){
            throw new FcfsEventException(ErrorCode.INVALID_EVENT_TIME);
        }

        final String fcfsId = FcfsUtil.quantityKeyFormatting(eventSequence.toString());
        if (isEventEnded(fcfsId)) { // 불필요한 Lock 접근을 막기 위한 flag 확인
            log.info("{} 선착순 이벤트 마감", fcfsId);
            return false;
        }

        final RLock lock = redissonClient.getLock(fcfsId);

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

            redissonClient.getBucket(fcfsId).set(quantity);
            stringRedisTemplate.opsForList().rightPush(fcfsId, userId);
            log.info("{} - 잔여 쿠폰: {}", Thread.currentThread().getName(), availableCoupons(fcfsId));
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

    private int availableCoupons(String key) {
        return (int) redissonClient.getBucket(key).get();
    }

    private boolean isEventEnded(String fcfsId) {
        return redissonClient.getBucket(FcfsUtil.endFlagFormatting(fcfsId)).get() != null;
    }

    private void endEvent(String fcfsId) {
        redissonClient.getBucket(FcfsUtil.endFlagFormatting(fcfsId)).set(true);
    }
}
