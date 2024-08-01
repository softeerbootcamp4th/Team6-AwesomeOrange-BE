package hyundai.softeer.orange.event.common.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.common.component.eventFieldMapper.EventFieldMapperMatcher;
import hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper.EventFieldMapper;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventStatus;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.common.exception.EventException;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.event.common.repository.EventMetadataRepository;
import hyundai.softeer.orange.event.component.EventKeyGenerator;
import hyundai.softeer.orange.event.dto.EventDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * 이벤트 전반에 대한 조회, 수정을 다루는 서비스. 구체적인 액션(추첨 등)은 구체적인 클래스에서 처리 요망
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EventService {
    private final EventFrameRepository efRepository;
    private final EventMetadataRepository emRepository;
    private final EventFieldMapperMatcher mapperMatcher;
    private final EventKeyGenerator keyGenerator;

    @Transactional
    public void createEvent(EventDto eventDto) {
        // 1. eventframe을 찾는다. 없으면 작업이 의미 X
        Optional<EventFrame> frameOpt = efRepository.findByName(eventDto.getTag());
        if (frameOpt.isEmpty()) throw new EventException(ErrorCode.EVENT_FRAME_NOT_FOUND);

        EventFrame frame = frameOpt.get();
        String eventKey = keyGenerator.generate();

        // 2. 이벤트 메타데이터 객체를 생성한다.
        EventMetadata eventMetadata = EventMetadata.builder()
                .eventId(eventKey)
                .name(eventDto.getName())
                .description(eventDto.getDescription())
                .startTime(eventDto.getStartTime())
                .endTime(eventDto.getEndTime())
                .url(eventDto.getUrl())
                .eventType(eventDto.getEventType())
                .status(EventStatus.READY) // 아직 시작 안함.
                .eventFrame(frame)
                .build();

        EventType type = eventDto.getEventType();
        EventFieldMapper mapper = mapperMatcher.getMapper(type);

        if(mapper == null) throw new EventException(ErrorCode.INVALID_EVENT_TYPE);
        mapper.handle(eventMetadata, eventDto);

        emRepository.save(eventMetadata);
    }

    @Transactional
    public void createEventFrame(String name) {
        EventFrame eventFrame = EventFrame.of(name);
        efRepository.save(eventFrame);
    }
}
