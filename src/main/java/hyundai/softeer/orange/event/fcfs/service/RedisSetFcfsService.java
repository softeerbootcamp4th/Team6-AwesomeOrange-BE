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
        // 이벤트 참여 가능 여부 확인
        validateEvent(eventSequence, userId);

        // 인원 마감 여부 확인
        if (isEventEnd(eventSequence)) {
            return false;
        }

        // 대기열에 등록
        stringRedisTemplate.opsForZSet().add(FcfsUtil.winnerFormatting(eventSequence.toString()), userId, System.currentTimeMillis());
        log.info("{} 선착순 이벤트 참여 성공", userId);
        return true;
    }

    private void validateEvent(Long eventSequence, String userId) {
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
    }

    // 이미 종료된 이벤트인지 확인하며, synchronized를 사용하여 동시성 문제 방지
    private synchronized boolean isEventEnd(Long eventSequence) {
        if(Boolean.TRUE.equals(booleanRedisTemplate.opsForValue().get(FcfsUtil.endFlagFormatting(eventSequence.toString())))){
            return true;
        }

        Long nowCount = stringRedisTemplate.opsForZSet().size(FcfsUtil.winnerFormatting(eventSequence.toString()));
        if(nowCount == null){
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }
        Integer maxNumber = numberRedisTemplate.opsForValue().get(FcfsUtil.keyFormatting(eventSequence.toString()));
        if (maxNumber == null) {
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }

        if(nowCount >= maxNumber){
            booleanRedisTemplate.opsForValue().set(FcfsUtil.endFlagFormatting(eventSequence.toString()), true);
            return true;
        }
        return false;
    }
}
