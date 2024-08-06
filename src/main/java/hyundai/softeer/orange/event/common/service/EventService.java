package hyundai.softeer.orange.event.common.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.common.EventConst;
import hyundai.softeer.orange.event.common.component.eventFieldMapper.EventFieldMapperMatcher;
import hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper.EventFieldMapper;
import hyundai.softeer.orange.event.common.component.query.EventSearchQueryParser;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventStatus;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.common.exception.EventException;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.event.common.repository.EventMetadataRepository;
import hyundai.softeer.orange.event.component.EventKeyGenerator;
import hyundai.softeer.orange.event.dto.BriefEventDto;
import hyundai.softeer.orange.event.dto.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
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

    /**
     * 이벤트를 생성한다.
     * @param eventDto 이벤트 dto
     */
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

    /**
     * 이벤트를 수정한다.
     * @param eventDto 수정 데이터가 담긴 이벤트 dto
     */
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

    /**
     * 이벤트에 대한 초기 데이터 정보를 제공한다.
     * @param eventId 요청한 이벤트의 id
     * @return 이벤트 내용을 담은 dto
     */
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

    /**
     * 매칭되는 이벤트를 탐색한다
     * @param search 이벤트 검색 내용
     * @param sortQuery 정렬 내용이 담긴 쿼리
     * @param page 현재 페이지
     * @param size 페이지의 크기
     * @return 매칭된 이벤트 목록
     */
    @Transactional(readOnly = true)
    public List<BriefEventDto> searchEvents(String search, String sortQuery, Integer page, Integer size) {

        List<Sort.Order> orders = new ArrayList<>();
        for(var entries: EventSearchQueryParser.parse(sortQuery).entrySet()){
            String field = entries.getKey();
            String value = entries.getValue().toLowerCase();

            if(!EventConst.sortableFields.contains(field)) continue;
            switch (value) {
                case "asc": case "":
                    orders.add(Sort.Order.asc(field));
                    break;
                case "desc":
                    orders.add(Sort.Order.desc(field));
                    break;
            }
        }
        Sort sort = Sort.by(orders);

        PageRequest pageInfo = PageRequest.of(
                page != null ? page : EventConst.EVENT_DEFAULT_PAGE,
                size != null ? size : EventConst.EVENT_DEFAULT_SIZE,
                sort
        );

        List<EventMetadata> events = emRepository.findAllBySearch(search, pageInfo);

        return events.stream().map(
                it -> BriefEventDto.of(
                        it.getEventId(),
                        it.getName(),
                        it.getStartTime(),
                        it.getEndTime(),
                        it.getEventType()
                )).toList();
    }

    /**
     * 이벤트 프레임을 생성한다.
     * @param name 이벤트 프레임의 이름
     */
    @Transactional
    public void createEventFrame(String name) {
        EventFrame eventFrame = EventFrame.of(name);
        efRepository.save(eventFrame);
    }

}
