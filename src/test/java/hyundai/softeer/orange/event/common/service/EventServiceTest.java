package hyundai.softeer.orange.event.common.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.common.component.eventFieldMapper.EventFieldMapperMatcher;
import hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper.FcfsEventFieldMapper;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.common.exception.EventException;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.event.common.repository.EventMetadataRepository;
import hyundai.softeer.orange.event.component.EventKeyGenerator;
import hyundai.softeer.orange.event.dto.EventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class EventServiceTest {
    private EventService eventService;
    private EventFrameRepository efRepo;
    private EventMetadataRepository emRepo;
    private EventFieldMapperMatcher mapperMatcher;
    private EventKeyGenerator keyGenerator;

    @BeforeEach
    void setUp() {
        efRepo = mock(EventFrameRepository.class);
        emRepo = mock(EventMetadataRepository.class);
        mapperMatcher = mock(EventFieldMapperMatcher.class);
        keyGenerator = mock(EventKeyGenerator.class);
        eventService = new EventService(efRepo, emRepo, mapperMatcher, keyGenerator);
    }

    @DisplayName("대응되는 eventframe이 없으면 예외 반환")
    @Test
    void createEvent_throwErrorIFNoMatchedEventFrame() {
        EventDto eventDto = EventDto.builder().build();

        assertThatThrownBy(() -> {
            eventService.createEvent(eventDto);
        }).isInstanceOf(EventException.class)
                .hasMessage(ErrorCode.EVENT_FRAME_NOT_FOUND.getMessage());
    }

    @DisplayName("이벤트 타입에 매칭되는 매퍼가 없다면 예외 반환")
    @Test
    void createEvent_throwErrorIFNoMatchedEventMapper() {
        EventDto eventDto = mock(EventDto.class);
        when(eventDto.getTag()).thenReturn("testtag");
        when(eventDto.getEventType()).thenReturn(EventType.fcfs);
        when(efRepo.findByName(anyString())).thenReturn(Optional.of(EventFrame.of("test")));

        assertThatThrownBy(() -> {
            eventService.createEvent(eventDto);
        }).isInstanceOf(EventException.class)
                .hasMessage(ErrorCode.INVALID_EVENT_TYPE.getMessage());
    }

    @DisplayName("이벤트 타입에 매칭되는 매퍼도 있다면 정상적으로 실행됨.")
    @Test
    void createEvent_presistEventMetadataSuccessfully() {
        EventDto eventDto = mock(EventDto.class);
        when(eventDto.getTag()).thenReturn("testtag");
        when(eventDto.getEventType()).thenReturn(EventType.fcfs);
        when(efRepo.findByName(anyString())).thenReturn(Optional.of(EventFrame.of("test")));
        when(mapperMatcher.getMapper(any(EventType.class))).thenReturn(mock(FcfsEventFieldMapper.class));

        eventService.createEvent(eventDto);

        verify(emRepo, times(1)).save(any(EventMetadata.class));
    }
}