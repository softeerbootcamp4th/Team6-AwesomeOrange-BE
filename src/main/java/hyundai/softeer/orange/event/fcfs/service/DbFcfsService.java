package hyundai.softeer.orange.event.fcfs.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEventWinningInfo;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventRepository;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventWinningInfoRepository;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    @Transactional
    public boolean participate(Long eventSequence, String userId){
        FcfsEvent fcfsEvent = fcfsEventRepository.findByIdWithLock(eventSequence)
                .orElseThrow(() -> new FcfsEventException(ErrorCode.EVENT_NOT_FOUND));
        EventUser eventUser = eventUserRepository.findByUserId(userId)
                .orElseThrow(() -> new FcfsEventException(ErrorCode.EVENT_USER_NOT_FOUND));
        // 이미 마감된 이벤트인지 확인
        if(fcfsEvent.getInfos().size() >= fcfsEvent.getParticipantCount()){
            return false;
        }
        validateParticipate(fcfsEvent, eventUser);

        fcfsEventWinningInfoRepository.save(FcfsEventWinningInfo.of(fcfsEvent, eventUser));
        return true;
    }

    private void validateParticipate(FcfsEvent fcfsEvent, EventUser eventUser){
        // 잘못된 이벤트 참여 시간인지 검증
        if(LocalDateTime.now().isBefore(fcfsEvent.getStartTime()) || LocalDateTime.now().isAfter(fcfsEvent.getEndTime())){
            throw new FcfsEventException(ErrorCode.INVALID_EVENT_TIME);
        }

        // 이미 당첨된 사용자인지 확인
        if(fcfsEvent.getInfos().stream().anyMatch(info -> info.getEventUser().equals(eventUser))){
            throw new FcfsEventException(ErrorCode.ALREADY_WINNER);
        }
    }
}
