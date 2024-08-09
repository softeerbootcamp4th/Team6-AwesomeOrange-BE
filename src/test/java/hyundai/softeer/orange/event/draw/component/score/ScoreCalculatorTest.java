package hyundai.softeer.orange.event.draw.component.score;

import hyundai.softeer.orange.comment.dto.WriteCommentCountDto;
import hyundai.softeer.orange.comment.repository.CommentRepository;
import hyundai.softeer.orange.event.draw.component.score.actionHandler.ActionHandler;
import hyundai.softeer.orange.event.draw.component.score.actionHandler.ParticipateEventActionHandler;
import hyundai.softeer.orange.event.draw.component.score.actionHandler.WriteCommentActionHandler;
import hyundai.softeer.orange.event.draw.dto.EventParticipateCountDto;
import hyundai.softeer.orange.event.draw.entity.DrawEventScorePolicy;
import hyundai.softeer.orange.event.draw.enums.DrawEventAction;
import hyundai.softeer.orange.event.draw.repository.EventParticipationInfoRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScoreCalculatorTest {
    @DisplayName("유저의 점수 기록을 계산하여 반환. 제대로 계산하는지 검사")
    @Test
    void calculateScore() {
        // 유저 참여 정보 mocking
        EventParticipateCountDto dto1 = mock(EventParticipateCountDto.class);
        when(dto1.getEventUserId()).thenReturn(1L);
        when(dto1.getCount()).thenReturn(3L);
        EventParticipateCountDto dto2 = mock(EventParticipateCountDto.class);
        when(dto2.getEventUserId()).thenReturn(2L);
        when(dto2.getCount()).thenReturn(4L);
        EventParticipateCountDto dto3 = mock(EventParticipateCountDto.class);
        when(dto3.getEventUserId()).thenReturn(3L);
        when(dto3.getCount()).thenReturn(5L);

        var mockRepo = mock(EventParticipationInfoRepository.class);
        when(mockRepo.countPerEventUserByEventId(anyLong()))
                .thenReturn(List.of(dto1, dto2, dto3));

        var epiHandler = new ParticipateEventActionHandler(mockRepo);

        WriteCommentCountDto dto4 = mock(WriteCommentCountDto.class);
        when(dto4.getEventUserId()).thenReturn(2L);
        when(dto4.getCount()).thenReturn(10L);
        WriteCommentCountDto dto5 = mock(WriteCommentCountDto.class);
        when(dto5.getEventUserId()).thenReturn(3L);
        when(dto5.getCount()).thenReturn(6L);
        WriteCommentCountDto dto6 = mock(WriteCommentCountDto.class);
        when(dto6.getEventUserId()).thenReturn(4L);
        when(dto6.getCount()).thenReturn(1L);

        var mockRepo2 = mock(CommentRepository.class);
        when(mockRepo2.countPerEventUserByEventId(anyLong()))
                .thenReturn(List.of(dto4, dto5, dto6));

        var comHandler = new WriteCommentActionHandler(mockRepo2);


        Map<DrawEventAction, ActionHandler> handlerMap = new HashMap<>();
        handlerMap.put(DrawEventAction.ParticipateEvent, epiHandler);
        handlerMap.put(DrawEventAction.WriteComment, comHandler);

        ScoreCalculator scoreCalculator = new ScoreCalculator(handlerMap);
        List<DrawEventScorePolicy> policies = List.of(
                DrawEventScorePolicy.of(DrawEventAction.ParticipateEvent, 5, null),
                DrawEventScorePolicy.of(DrawEventAction.WriteComment, 3, null)
        );

        var resultMap = scoreCalculator.calculate(anyLong(), policies);
        assertThat(resultMap).isNotNull();

        assertThat(resultMap.size()).isEqualTo(4);
        assertThat(resultMap.get(1L)).isEqualTo(15L);   // 3 * 5          = 15
        assertThat(resultMap.get(2L)).isEqualTo(50L);   // 4 * 5 + 10 * 3 = 50
        assertThat(resultMap.get(3L)).isEqualTo(43L);   // 5 * 5 +  6 * 3 = 43
        assertThat(resultMap.get(4L)).isEqualTo(3L);    //          1 * 3 =  3
    }
}