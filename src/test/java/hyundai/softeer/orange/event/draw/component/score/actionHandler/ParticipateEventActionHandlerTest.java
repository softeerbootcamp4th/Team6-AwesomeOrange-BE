package hyundai.softeer.orange.event.draw.component.score.actionHandler;

import hyundai.softeer.orange.event.draw.dto.EventParticipateCountDto;
import hyundai.softeer.orange.event.draw.repository.EventParticipationInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ParticipateEventActionHandlerTest {
    @DisplayName("사용자에 대한 점수 * 참여 수 설정")
    @Test
    void checkUserScoreXCountSet() {
        EventParticipateCountDto dto1 = mock(EventParticipateCountDto.class);
        when(dto1.getEventUserId()).thenReturn(1L);
        when(dto1.getCount()).thenReturn(10L);
        EventParticipateCountDto dto2 = mock(EventParticipateCountDto.class);
        when(dto2.getEventUserId()).thenReturn(2L);
        when(dto2.getCount()).thenReturn(3L);
        EventParticipateCountDto dto3 = mock(EventParticipateCountDto.class);
        when(dto3.getEventUserId()).thenReturn(3L);
        when(dto3.getCount()).thenReturn(5L);

        var mockRepo = mock(EventParticipationInfoRepository.class);
        when(mockRepo.countPerEventUserByEventId(anyLong()))
                .thenReturn(List.of(dto1, dto2, dto3));

        ParticipateEventActionHandler handler = new ParticipateEventActionHandler(mockRepo);
        Map<Long, Long> scoreMap = new HashMap<>();

        long score = 3L;
        handler.handle(scoreMap, anyLong(), score);

        assertThat(scoreMap).hasSize(3);
        assertThat(scoreMap.get(1L)).isEqualTo(score * 10L);
        assertThat(scoreMap.get(2L)).isEqualTo(score * 3L);
        assertThat(scoreMap.get(3L)).isEqualTo(score * 5L);
    }
}