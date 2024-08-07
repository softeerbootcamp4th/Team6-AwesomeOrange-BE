package hyundai.softeer.orange.event.dto;

import lombok.Getter;

/**
 * 관리자가 이벤트 댓글 검색 시 자동완성 영역에 제공되는 데이터
 */
public interface EventSearchHintDto {
    String getEventId();
    String getName();
}
