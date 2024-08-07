package hyundai.softeer.orange.event.common.service;

import hyundai.softeer.orange.event.common.component.eventFieldMapper.EventFieldMapperMatcher;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.event.common.repository.EventMetadataRepository;
import hyundai.softeer.orange.event.component.EventKeyGenerator;
import hyundai.softeer.orange.event.draw.entity.DrawEvent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class EventServiceSearchHintsTest {
    @Autowired
    private EventMetadataRepository emRepo;
    @Autowired
    private EventFrameRepository efRepo;

    private EventFieldMapperMatcher mapperMatcher;
    private EventKeyGenerator keyGenerator;
    private EventService eventService;

    @BeforeEach
    public void setUp() {
        mapperMatcher = mock(EventFieldMapperMatcher.class);
        keyGenerator = mock(EventKeyGenerator.class);
        eventService = new EventService(efRepo, emRepo, mapperMatcher, keyGenerator);
        // 이벤트 프레임 생성
        EventFrame ef = EventFrame.of("test");
        efRepo.save(ef);

        // 이벤트 메타데이터 생성
        EventMetadata em1 = EventMetadata.builder()
                .eventId("HD240805_001")
                .name("hyundai car event")
                .startTime(LocalDateTime.of(2024,8,1,15,0))
                .endTime(LocalDateTime.of(2024,8,2,15,0))
                .eventFrame(ef)
                .eventType(EventType.draw)
                .build();

        EventMetadata em2 = EventMetadata.builder()
                .eventId("HD240905_002")
                .name("hyundai car event")
                .startTime(LocalDateTime.of(2024,8,1,15,0))
                .endTime(LocalDateTime.of(2024,8,2,15,0))
                .eventFrame(ef)
                .eventType(EventType.draw)
                .build();

        EventMetadata em3 = EventMetadata.builder()
                .eventId("HD240805_003")
                .name("hyundai car event")
                .startTime(LocalDateTime.of(2024,8,1,15,0))
                .endTime(LocalDateTime.of(2024,8,2,15,0))
                .eventFrame(ef)
                .eventType(EventType.fcfs)
                .build();

        // 이벤트 프레임 리스트 등록
        emRepo.saveAll(List.of(em1, em2, em3));
    }

    @DisplayName("검색어가 없으면 아무 것도 반환 안함")
    @Test
    void searchHints_NoReturnIfNoSearch() {

        var hints = eventService.searchHints(null);
        assertThat(hints).isEmpty();
    }

    @DisplayName("검색어가 있더라도 매칭되는 값 없으면 반환 안함")
    @Test
    void searchHints_NoReturnIfNoMatchedToSearch() {
        String search = "NotMatch";
        var hints = eventService.searchHints(search);
        assertThat(hints).isEmpty();
    }


    @DisplayName("추첨 이벤트만 매칭됨")
    @Test
    void searchHints_MatchOnlyDrawEvent() {
        String search = "240805";
        var hints = eventService.searchHints(search);
        assertThat(hints).hasSize(1);
        assertThat(hints.get(0).getEventId()).isEqualTo("HD240805_001");
    }

    @DisplayName("검색어에 따라 매칭되는 이벤트 목록이 달라짐")
    @Test
    void searchHints_MatchVariousBySearch() {
        String search1 = "240";
        var hints1 = eventService.searchHints(search1);
        assertThat(hints1).hasSize(2);

        String search2 = "2409";
        var hints2 = eventService.searchHints(search2);
        assertThat(hints2).hasSize(1);

    }
}
