package hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.dto.EventDto;
import hyundai.softeer.orange.event.dto.fcfs.FcfsEventDto;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FcfsEventFieldMapperTest {

    FcfsEventFieldMapper mapper;
    @BeforeEach
    void setUp() {
        FcfsEventRepository repo = mock(FcfsEventRepository.class);
        mapper = new FcfsEventFieldMapper(repo);
    }

    @DisplayName("canHandle은 해당 타입을 지원하는지 여부를 반환한다.")
    @Test
    void canHandle_returnTypeSupported() {
        EventType supported = EventType.fcfs;
        EventType unsupported = EventType.draw;

        assertThat(mapper.canHandle(supported)).isTrue();
        assertThat(mapper.canHandle(unsupported)).isFalse();
    }

    @DisplayName("EventDto에 Fcfs 필드가 없으면 예외가 터진다.")
    @Test
    void throwErrorIfFcfsDtoNotExists() {
        EventMetadata metadata = new EventMetadata();
        EventDto dto1 = new EventDto(); // fcfsdto 없음
        EventDto dto2 = mock(EventDto.class);
        when(dto2.getFcfs()).thenReturn(List.of());

        assertThatThrownBy(() -> {
            mapper.fetchToEventEntity(metadata, dto1);
        });

        assertThatThrownBy(() -> {
            mapper.fetchToEventEntity(metadata, dto2);
        });
    }

    @DisplayName("EventDto에 값이 있다면 정상적으로 관계를 설정한다.")
    @Test
    void setRelationIfFcfsDtoExists() {
        EventMetadata metadata = new EventMetadata();
        EventDto dto = mock(EventDto.class);
        List<FcfsEventDto> dtos = List.of(
                new FcfsEventDto(),
                new FcfsEventDto()
        );
        when(dto.getFcfs()).thenReturn(dtos);

        mapper.fetchToEventEntity(metadata, dto);

        List<FcfsEvent> fcfsEvents = metadata.getFcfsEventList();

        assertThat(fcfsEvents).isNotNull().hasSize(2);
        assertThat(fcfsEvents.get(0).getEventMetaData()).isSameAs(metadata);
    }
}