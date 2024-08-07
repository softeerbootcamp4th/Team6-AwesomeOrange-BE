package hyundai.softeer.orange.event.dto.fcfs;

import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * 선착순 이벤트를 표현하는 객체
 */
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class FcfsEventDto {
    /**
     * Fcfs 이벤트의 id. 서버 db 측에서 사용하기 위한 값
     */
    private Long id;

    /**
     * 시작 시간
     */
    @NotNull
    private LocalDateTime startTime;

    /**
     * 종료 시간
     */
    @NotNull
    private LocalDateTime endTime;

    /**
     * 당첨 인원
     */
    @NotNull
    private Long participantCount;

    /**
     * 상품 관련된 정보를 저장하는 영역
     */
    @NotBlank
    private String prizeInfo;
}