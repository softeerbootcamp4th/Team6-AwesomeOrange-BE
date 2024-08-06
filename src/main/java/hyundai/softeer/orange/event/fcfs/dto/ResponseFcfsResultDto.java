package hyundai.softeer.orange.event.fcfs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
public class ResponseFcfsResultDto {

    // 선착순 이벤트 정답 여부
    private boolean answerResult;

    // 선착순 이벤트 당첨 여부, answerResult가 false라면 무조건 false를 반환
    private boolean isWinner;
}
