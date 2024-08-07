package hyundai.softeer.orange.event.dto;

import hyundai.softeer.orange.event.common.enums.EventType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 이벤트 리스트를 위한 정보만 담고 있는 객체
 */
public interface BriefEventDto {
    /**
     * HD000000_000 형식으로 구성된 id 값
     */
    String getEventId();
    /**
     * 이벤트의 이름
     */
    String getName();

    /**
     * 이벤트 시작 시간
     */
    LocalDateTime getStartTime();

    /**
     * 이벤트 종료 시간
     */
    LocalDateTime getEndTime();

    /**
     * 이벤트의 타입
     */
    EventType getEventType();
}
