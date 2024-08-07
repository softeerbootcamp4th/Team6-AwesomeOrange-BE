package hyundai.softeer.orange.event.fcfs.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.fcfs.dto.ResponseFcfsInfoDto;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcfsAnswerService {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean judgeAnswer(Long eventSequence, String answer) {
        String correctAnswer = stringRedisTemplate.opsForValue().get(FcfsUtil.answerFormatting(eventSequence.toString()));
        if (correctAnswer == null) {
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }
        return correctAnswer.equals(answer);
    }

    public ResponseFcfsInfoDto getFcfsInfo(Long eventSequence) {
        String startTime = stringRedisTemplate.opsForValue().get(FcfsUtil.startTimeFormatting(eventSequence.toString()));
        // 선착순 이벤트가 존재하지 않는 경우
        if (startTime == null) {
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }

        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDateTime eventStartTime = LocalDateTime.parse(startTime);

        // 서버시간 < 이벤트시작시간 < 서버시간+3시간 -> countdown
        // 이벤트시작시간 < 서버시간 < 이벤트시작시간+7시간 -> progress
        // 그 외 -> waiting
        if(nowDateTime.isBefore(eventStartTime) && nowDateTime.plusHours(3).isAfter(eventStartTime)) {
            return new ResponseFcfsInfoDto(nowDateTime, "countdown");
        } else if(eventStartTime.isBefore(nowDateTime) && eventStartTime.plusHours(7).isAfter(nowDateTime)) {
            return new ResponseFcfsInfoDto(nowDateTime, "progress");
        } else {
            return new ResponseFcfsInfoDto(nowDateTime, "waiting");
        }
    }
}
