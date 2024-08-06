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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        EventFrame frame = frameOpt
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_FRAME_NOT_FOUND));

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

        mapper.fetchToEventEntity(eventMetadata, eventDto);

        emRepository.save(eventMetadata);
    }

    @Transactional
    public void editEvent(EventDto eventDto) {
        String eventId = eventDto.getEventId();
        Optional<EventMetadata> metadataOpt = emRepository.findFirstByEventId(eventId);
        EventMetadata eventMetadata = metadataOpt
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));
        eventMetadata.updateName(eventDto.getName());
        eventMetadata.updateDescription(eventDto.getDescription());
        eventMetadata.updateStartTime(eventDto.getStartTime());
        eventMetadata.updateEndTime(eventDto.getEndTime());
        eventMetadata.updateUrl(eventDto.getUrl());

        EventFieldMapper mapper = mapperMatcher.getMapper(eventDto.getEventType());
        if(mapper == null) throw new EventException(ErrorCode.INVALID_EVENT_TYPE);

        mapper.editEventField(eventMetadata, eventDto);
        emRepository.save(eventMetadata);
    }

    @Transactional(readOnly = true)
    public EventDto getEventInfo(String eventId) {
        Optional<EventMetadata> metadataOpt = emRepository.findFirstByEventId(eventId);
        EventMetadata metadata = metadataOpt
                .orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));

        EventFieldMapper mapper = mapperMatcher.getMapper(metadata.getEventType());
        if(mapper == null) throw new EventException(ErrorCode.INVALID_EVENT_TYPE);

        EventDto eventDto = EventDto.builder()
                .id(metadata.getId())
                .eventId(metadata.getEventId())
                .name(metadata.getName())
                .description(metadata.getDescription())
                .url(metadata.getUrl())
                .startTime(metadata.getStartTime())
                .endTime(metadata.getEndTime())
                .eventType(metadata.getEventType())
                .build();

        mapper.fetchToDto(metadata, eventDto);
        return eventDto;
    }

    @Transactional
    public void createEventFrame(String name) {
        EventFrame eventFrame = EventFrame.of(name);
        efRepository.save(eventFrame);
    }

}
