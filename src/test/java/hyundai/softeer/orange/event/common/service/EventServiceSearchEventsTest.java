package hyundai.softeer.orange.event.common.service;

import hyundai.softeer.orange.event.common.component.eventFieldMapper.EventFieldMapperMatcher;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.event.common.repository.EventMetadataRepository;
import hyundai.softeer.orange.event.component.EventKeyGenerator;
import hyundai.softeer.orange.event.dto.BriefEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
public class EventServiceSearchEventsTest {
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
        EventFrame ef = EventFrame.of("test");
        efRepo.save(ef);
        EventMetadata em1 = EventMetadata.builder()
                .eventId("HD240805_001")
                .name("hyundai car event")
                .startTime(LocalDateTime.of(2024,8,1,15,0))
                .endTime(LocalDateTime.of(2024,8,2,15,0))
                .eventFrame(ef)
                .build();

        EventMetadata em2 = EventMetadata.builder()
                .eventId("HD240805_002")
                .name("hello bye")
                .startTime(LocalDateTime.of(2024,8,1,15,0))
                .endTime(LocalDateTime.of(2024,8,2,17,0))
                .eventFrame(ef)
                .build();

        EventMetadata em3 = EventMetadata.builder()
                .eventId("HD240805_003")
                .name("hyundai car event")
                .startTime(LocalDateTime.of(2024,8,1,18,0))
                .endTime(LocalDateTime.of(2024,8,1,20,0))
                .eventFrame(ef)
                .build();

        EventMetadata em4 = EventMetadata.builder()
                .eventId("HD240805_004")
                .name("25 always opened")
                .startTime(LocalDateTime.of(2024,8,1,19,0))
                .endTime(LocalDateTime.of(2024,8,1,20,0))
                .eventFrame(ef)
                .build();

        EventMetadata em5 = EventMetadata.builder()
                .eventId("HD240805_005")
                .name("zebra car event")
                .startTime(LocalDateTime.of(2024,8,1,21,0))
                .endTime(LocalDateTime.of(2024,8,1,22,0))
                .eventFrame(ef)
                .build();

        EventMetadata em6 = EventMetadata.builder()
                .eventId("HD240805_006")
                .name("25 always opened")
                .startTime(LocalDateTime.of(2024,8,1,22,0))
                .endTime(LocalDateTime.of(2024,8,1,23,0))
                .eventFrame(ef)
                .build();

        emRepo.save(em1);
        emRepo.save(em2);
        emRepo.save(em3);
        emRepo.save(em4);
        emRepo.save(em5);
        emRepo.save(em6);
    }

    @DisplayName("search 없으면 모두 출력")
    @Test
    void searchEvents_findAllIfSearchIsNull() {
        var list = eventService.searchEvents(null, null, null, null);
        assertThat(list).hasSize(5);
    }

    @DisplayName("search 있으면 매칭되는 값 출력")
    @Test
    void searchEvents_findMatchedIfSearchExists() {
        var list = eventService.searchEvents("hyundai", null, null, null);
        assertThat(list).hasSize(2);
    }

    @DisplayName("search 있더라도 매칭되는 것 없으면 아무것도 반환 안함")
    @Test
    void searchEvents_findNothingIfSearchExistsButNotMatch() {
        var list = eventService.searchEvents("not-exist", null, null, null);
        assertThat(list).hasSize(0);
    }

    @DisplayName("정렬 옵션 있으면 정렬된 형태로 반환")
    @Test
    void searchEvents_returnOrdered() {
        String query = "startTime,endTime:desc,error";
        // startTime은 존재, 기본 값 asc
        // endTime은 존재, desc
        // error은 존재 X, 맞지 않는 값은 그냥 무시

        var list = eventService.searchEvents(null, "startTime,endTime:desc,error", null, null);
        BriefEventDto target = list.get(0);

        assertThat(target.getEventId()).isEqualTo("HD240805_002");
    }

    @DisplayName("페이지 옵션 있다면 해당 데이터 반환")
    @Test
    void searchEvents_returnPaged() {
        String query = "startTime,endTime:desc,error";
        // startTime은 존재, 기본 값 asc
        // endTime은 존재, desc
        // error은 존재 X, 맞지 않는 값은 그냥 무시

        var list = eventService.searchEvents(null, "eventId", 1, 2);

        assertThat(list.get(0).getEventId()).isEqualTo("HD240805_003");
        assertThat(list.get(1).getEventId()).isEqualTo("HD240805_004");
    }

    @DisplayName("여러 옵션 함께 사용도 가능")
    @Test
    void searchEvents_withMultipleOptions() {

        var list = eventService.searchEvents("25", "endTime:desc", 1, 1);
        BriefEventDto target = list.get(0);

        assertThat(list).hasSize(1);
        assertThat(target.getEventId()).isEqualTo("HD240805_004");
    }
}
