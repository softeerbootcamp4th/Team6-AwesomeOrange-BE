package hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.draw.repository.DrawEventMetadataRepository;
import hyundai.softeer.orange.event.draw.repository.DrawEventScorePolicyRepository;
import hyundai.softeer.orange.event.dto.EventDto;
import hyundai.softeer.orange.event.dto.draw.DrawEventDto;
import hyundai.softeer.orange.event.dto.draw.DrawEventMetadataDto;
import hyundai.softeer.orange.event.dto.draw.DrawEventScorePolicyDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
class DrawEventFieldMapperTest {
    @Autowired
    DrawEventMetadataRepository drawEventMetadataRepository;
    @Autowired
    DrawEventScorePolicyRepository drawEventScorePolicyRepository;
    DrawEventFieldMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DrawEventFieldMapper(drawEventMetadataRepository, drawEventScorePolicyRepository);
    }


    @DisplayName("canHandle은 해당 타입을 지원하는지 여부를 반환한다.")
    @Test
    void canHandle_returnTypeSupported() {
        EventType supported = EventType.draw;
        EventType unsupported = EventType.fcfs;

        assertThat(mapper.canHandle(supported)).isTrue();
        assertThat(mapper.canHandle(unsupported)).isFalse();
    }

    @DisplayName("EventDto에 draw 필드가 없으면 예외가 터진다.")
    @Test
    void fetchToEventEntity_throwErrorIfFcfsDtoNotExists() {
        EventMetadata metadata = new EventMetadata();
        EventDto dto1 = new EventDto(); // drawDto 없음

        assertThatThrownBy(() -> {
            mapper.fetchToEventEntity(metadata, dto1);
        });
    }


    @DisplayName("정상적인 EventDto가 들어오면 정상적으로 관계를 설정한다.")
    @Test
    void fetchToEventEntity_setRelationIfEventDtoIsValid() {
        EventMetadata metadata = new EventMetadata();
        EventDto dto = mock(EventDto.class);
        DrawEventDto drawEventDto = mock(DrawEventDto.class);
        when(dto.getDraw()).thenReturn(drawEventDto);
        when(drawEventDto.getMetadata()).thenReturn(List.of(new DrawEventMetadataDto()));
        when(drawEventDto.getPolicies()).thenReturn(List.of(new DrawEventScorePolicyDto()));

        mapper.fetchToEventEntity(metadata, dto);

        var drawEvents = metadata.getDrawEventList();
        var drawEvent = drawEvents.get(0);
        var drawEventMetadata = drawEvent.getMetadataList();
        var oneDrawMetadata = drawEventMetadata.get(0);
        var drawEventPolicies  = drawEvent.getPolicyList();
        var oneEventPolicy = drawEventPolicies.get(0);

        assertThat(drawEvents).isNotNull().hasSize(1);
        assertThat(drawEventMetadata).isNotNull().hasSize(1);
        assertThat(drawEventPolicies).isNotNull().hasSize(1);
        // 부모 자식 관계 잘 설정하는지
        assertThat(oneDrawMetadata.getDrawEvent()).isSameAs(drawEvent);
        assertThat(oneEventPolicy.getDrawEvent()).isSameAs(drawEvent);
    }

    @DisplayName("draw 이벤트가 없으면 예외가 발생한다.")
    @Test
    void editEventField_throwIfDrawEventNotExists() {
        EventMetadata metadata = new EventMetadata();
        EventDto dto = mock(EventDto.class);
        assertThatThrownBy(() -> {
            mapper.editEventField(metadata, dto);
        });
    }

    // TODO: repository 통합 테스트 코드 작성
//    @DisplayName("규칙에 따라 정상적으로 DrawEvent를 갱신해야 한다.")
//    @Test
//    void editEventField_testOpIsValid() {
//        // 1. DrawEvent
//        EventMetadata metadata = new EventMetadata();
//        EventDto dto = new EventDto();
//    }
}