package hyundai.softeer.orange.event.draw.component.score.actionHandler;

import hyundai.softeer.orange.comment.dto.WriteCommentCountDto;
import hyundai.softeer.orange.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WriteCommentActionHandlerTest {
    @DisplayName("사용자가 작성한 댓글 수 * 참여 수 설정")
    @Test
    void checkUserScoreXCountSet() {
        WriteCommentCountDto dto1 = mock(WriteCommentCountDto.class);
        when(dto1.getEventUserId()).thenReturn(1L);
        when(dto1.getCount()).thenReturn(10L);
        WriteCommentCountDto dto2 = mock(WriteCommentCountDto.class);
        when(dto2.getEventUserId()).thenReturn(2L);
        when(dto2.getCount()).thenReturn(3L);
        WriteCommentCountDto dto3 = mock(WriteCommentCountDto.class);
        when(dto3.getEventUserId()).thenReturn(3L);
        when(dto3.getCount()).thenReturn(5L);

        var mockRepo = mock(CommentRepository.class);
        when(mockRepo.countPerEventUserByEventId(anyLong()))
                .thenReturn(List.of(dto1, dto2, dto3));

        WriteCommentActionHandler handler = new WriteCommentActionHandler(mockRepo);
        Map<Long, Long> scoreMap = new HashMap<>();

        long score = 3L;
        handler.handle(scoreMap, anyLong(), score);

        assertThat(scoreMap).hasSize(3);
        assertThat(scoreMap.get(1L)).isEqualTo(score * 10L);
        assertThat(scoreMap.get(2L)).isEqualTo(score * 3L);
        assertThat(scoreMap.get(3L)).isEqualTo(score * 5L);
    }
}