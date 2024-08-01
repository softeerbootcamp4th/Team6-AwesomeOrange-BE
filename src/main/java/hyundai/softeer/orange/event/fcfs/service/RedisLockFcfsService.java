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
        String startTime = stringRedisTemplate.opsForValue().get(FcfsUtil.startTimeFormatting(eventSequence.toString()));
        if(startTime == null) {
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }

        if (LocalDateTime.now().isBefore(LocalDateTime.parse(startTime))){
            throw new FcfsEventException(ErrorCode.INVALID_EVENT_TIME);
        }

        final String fcfsId = FcfsUtil.keyFormatting(eventSequence.toString());

        // 불필요한 Lock 접근을 막기 위한 종료 flag 확인
        if (isEventEnded(fcfsId)) {
            log.info("{} 선착순 이벤트 마감으로 참석 실패", userId);
            return false;
        }

        final RLock lock = redissonClient.getLock("LOCK:" + fcfsId);

        try {
            boolean usingLock = lock.tryLock(1L, 3L, TimeUnit.SECONDS);
            if (!usingLock) {
                return false;
            }

            final int quantity = availableCoupons(fcfsId);
            if (quantity <= 0) {
                log.info("{} - 쿠폰 소진으로 참석 실패", userId);
                endEvent(fcfsId);  // 이벤트 종료 플래그 설정
                return false;
            }

            redissonClient.getBucket(fcfsId).set(quantity-1);
            stringRedisTemplate.opsForSet().add(FcfsUtil.winnerFormatting(eventSequence.toString()), userId);
            log.info("{} - 잔여 쿠폰: {}", userId, availableCoupons(fcfsId));
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
