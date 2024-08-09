package hyundai.softeer.orange.event.draw.service;

import hyundai.softeer.orange.event.draw.component.picker.PickTarget;
import hyundai.softeer.orange.event.draw.component.picker.WinnerPicker;
import hyundai.softeer.orange.event.draw.component.score.ScoreCalculator;
import hyundai.softeer.orange.event.draw.dto.DrawEventWinningInfoBulkInsertDto;
import hyundai.softeer.orange.event.draw.entity.DrawEvent;
import hyundai.softeer.orange.event.draw.entity.DrawEventMetadata;
import hyundai.softeer.orange.event.draw.repository.DrawEventRepository;
import hyundai.softeer.orange.event.draw.repository.DrawEventWinningInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DrawEventServiceTest {

    private static final Logger log = LoggerFactory.getLogger(DrawEventServiceTest.class);

    @DisplayName("대응되는 이벤트가 존재하지 않으면 예외 반환")
    @Test
    void throwExceptionIfDrawEventNotFound() {
        var deRepository = mock(DrawEventRepository.class);
        when(deRepository.findById(anyLong())).thenReturn(Optional.empty());

        var deService = new DrawEventService(deRepository, null, null, null);

        assertThatThrownBy(() -> {
            deService.draw(0L);
        });
    }

    @DisplayName("대응되는 이벤트가 존재하면 작업 수행")
    @Test
    void drawEvent() {
        // draw event 처리
        var drawEvent = mock(DrawEvent.class);

        // 정확한 인원 수를 추첨하는지
        when(drawEvent.getMetadataList()).thenReturn(new ArrayList<>(List.of(
                // 합 = 5
                DrawEventMetadata.of(3L, 2L, null, null),
                DrawEventMetadata.of(1L, 1L, null, null),
                DrawEventMetadata.of(2L, 2L, null, null)
        )));
        when(drawEvent.getPolicyList()).thenReturn(List.of());

        var deRepository = mock(DrawEventRepository.class);
        when(deRepository.findById(anyLong())).thenReturn(Optional.of(drawEvent));

        // repository 모킹. saveMany 호출하는지 검사 필요
        var deWinningInfoRepository = mock(DrawEventWinningInfoRepository.class);

        // 점수 채점기 모킹
        var calculator = mock(ScoreCalculator.class);
        when(calculator.calculate(anyLong(), anyList())).thenReturn(Map.of(1L, 1L, 2L, 10L, 3L,5L));

        // 추첨기 모킹
        var picker = mock(WinnerPicker.class);
        when(picker.pick(anyList(), anyLong())).thenReturn(new ArrayList<>(
                List.of(
                new PickTarget(2L, 1L), // 1
                new PickTarget(1L, 2L), // 2
                new PickTarget(3L, 3L), // 2
                new PickTarget(5L, 1L), // 3
                new PickTarget(4L, 2L) // 3
        )));

        var deService = new DrawEventService(deRepository, deWinningInfoRepository, picker, calculator);

        deService.draw(0L);

        ArgumentCaptor<List<DrawEventWinningInfoBulkInsertDto>> ac = ArgumentCaptor.forClass(List.class);

        verify(picker, times(1)).pick(anyList(), eq(5L));
        verify(deWinningInfoRepository, times(1)).insertMany(ac.capture());

        var list = ac.getValue();
        assertThat(list).hasSize(5);
        assertThat(list.get(0).getEventUserId()).isEqualTo(2L);
        assertThat(list.get(1).getEventUserId()).isEqualTo(1L);
        assertThat(list.get(2).getEventUserId()).isEqualTo(3L);
        assertThat(list.get(3).getEventUserId()).isEqualTo(5L);
        assertThat(list.get(4).getEventUserId()).isEqualTo(4L);
        for(var v: list) {
            log.info("ranking {}", v.getRanking());
        }
        assertThat(list.get(0).getRanking()).isEqualTo(1L);
        assertThat(list.get(1).getRanking()).isEqualTo(2L);
        assertThat(list.get(2).getRanking()).isEqualTo(2L);
        assertThat(list.get(3).getRanking()).isEqualTo(3L);
        assertThat(list.get(4).getRanking()).isEqualTo(3L);
    }
}