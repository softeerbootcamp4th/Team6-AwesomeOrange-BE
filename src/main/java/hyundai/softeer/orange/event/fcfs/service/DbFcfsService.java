package hyundai.softeer.orange.event.fcfs.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEventWinningInfo;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventRepository;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventWinningInfoRepository;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class DbFcfsService implements FcfsService{

    private final FcfsEventRepository fcfsEventRepository;
    private final EventUserRepository eventUserRepository;
    private final FcfsEventWinningInfoRepository fcfsEventWinningInfoRepository;
    private final RedisTemplate<String, Boolean> booleanRedisTemplate;

    @Override
    @Transactional
    public boolean participate(Long eventSequence, String userId){
        // 이벤트 종료 여부 확인
        if (isEventEnded(eventSequence)) {
            return false;
        }

        // 비관적 락을 통해 동시성 제어
        FcfsEvent fcfsEvent = fcfsEventRepository.findByIdWithLock(eventSequence)
                .orElseThrow(() -> new FcfsEventException(ErrorCode.EVENT_NOT_FOUND));
        EventUser eventUser = eventUserRepository.findByUserId(userId)
                .orElseThrow(() -> new FcfsEventException(ErrorCode.EVENT_USER_NOT_FOUND));

        // 잘못된 이벤트 참여 시간인지 검증
        if(LocalDateTime.now().isBefore(fcfsEvent.getStartTime()) || LocalDateTime.now().isAfter(fcfsEvent.getEndTime())){
            throw new FcfsEventException(ErrorCode.INVALID_EVENT_TIME);
        }

        // 이미 이 이벤트에 참여했는지 확인
        if(isParticipated(eventUser.getId(), eventSequence)){
            throw new FcfsEventException(ErrorCode.ALREADY_PARTICIPATED);
        }

        // 인원 수 초과 시 종료 flag 설정
        if(fcfsEvent.getInfos().size() >= fcfsEvent.getParticipantCount()){
            log.info("Event Finished: {},", fcfsEvent.getInfos().size());
            endEvent(eventSequence);
            return false;
        }

        fcfsEventWinningInfoRepository.save(FcfsEventWinningInfo.of(fcfsEvent, eventUser));
        log.info("Participating Success: {}, User ID: {}", eventSequence, userId);
        return true;
    }

    private boolean isParticipated(Long userId, Long eventSequence){
        return fcfsEventWinningInfoRepository.existsByEventUserIdAndFcfsEventId(userId, eventSequence);
    }

    private boolean isEventEnded(Long eventSequence){
        return Boolean.TRUE.equals((booleanRedisTemplate.opsForValue().get(FcfsUtil.endFlagFormatting(eventSequence.toString()))));
    }

    private void endEvent(Long eventSequence){
        booleanRedisTemplate.opsForValue().set(FcfsUtil.endFlagFormatting(eventSequence.toString()), true);
    }
}
