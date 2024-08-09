package hyundai.softeer.orange.event.fcfs.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FcfsAnswerService {

    private final StringRedisTemplate stringRedisTemplate;

    public boolean judgeAnswer(Long eventSequence, String answer) {
        String correctAnswer = stringRedisTemplate.opsForValue().get(FcfsUtil.answerFormatting(eventSequence.toString()));
        if (correctAnswer == null) {
            throw new FcfsEventException(ErrorCode.FCFS_EVENT_NOT_FOUND);
        }
        return correctAnswer.trim().equals(answer.trim()); // 정답 비교 시 공백을 제거
    }
}
