package hyundai.softeer.orange.event.dto;

import lombok.Getter;

/**
 * 관리자가 이벤트 댓글 검색 시 자동완성 영역에 제공되는 데이터
 */
@Getter
public class EventSearchHintDto {
    private String eventId;
    private String name;

    public static EventSearchHintDto of(String eventId, String name) {
        EventSearchHintDto dto = new EventSearchHintDto();
        dto.eventId = eventId;
        dto.name = name;
        return dto;
    }
}
