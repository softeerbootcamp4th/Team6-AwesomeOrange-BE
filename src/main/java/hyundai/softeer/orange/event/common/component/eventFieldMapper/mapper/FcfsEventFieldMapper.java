package hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.common.exception.EventException;
import hyundai.softeer.orange.event.dto.EventDto;
import hyundai.softeer.orange.event.dto.fcfs.FcfsEventDto;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * EventMetadata에 FcfsEvent를 주입해주는 매퍼
 */
@RequiredArgsConstructor
@Component
public class FcfsEventFieldMapper implements EventFieldMapper {
    private final FcfsEventRepository fcfsEventRepository;
    @Override
    public boolean canHandle(EventType eventType) {
        return eventType.equals(EventType.fcfs);
    }

    @Override
    public void fetchToEventEntity(EventMetadata metadata, EventDto eventDto) {
        List<FcfsEventDto> fcfsDtos = eventDto.getFcfs();
        // 비어 있으면 안됨
        if (fcfsDtos == null || fcfsDtos.isEmpty()) throw new EventException(ErrorCode.INVALID_JSON);

        List<FcfsEvent> fcfsEventList = fcfsDtos.stream().map(
                it -> FcfsEvent.builder()
                        .startTime(it.getStartTime())
                        .endTime(it.getEndTime())
                        .participantCount(it.getParticipantCount())
                        .prizeInfo(it.getPrizeInfo())
                        .eventMetaData(metadata)
                        .build()
        ).toList();

        metadata.addFcfsEvents(fcfsEventList);
    }

    @Override
    public void fetchToDto(EventMetadata metadata, EventDto eventDto) {
        List<FcfsEvent> fcfsEvents = metadata.getFcfsEventList();
        List<FcfsEventDto> fcfsEventDtos = fcfsEvents.stream().map(it -> FcfsEventDto
                .builder()
                .id(it.getId())
                .startTime(it.getStartTime())
                .endTime(it.getEndTime())
                .participantCount(it.getParticipantCount())
                .prizeInfo(it.getPrizeInfo())
                .build()
        ).toList();
        eventDto.setFcfsList(fcfsEventDtos);
    }

    @Override
    public void editEventField(EventMetadata metadata, EventDto eventDto) {
        List<FcfsEvent> fcfsEvents = metadata.getFcfsEventList();
        // 집합을 이용해서 created / updated / deleted 를 구분
        Map<Boolean, Map<Long, FcfsEventDto>> fcfsAllDtos = eventDto.getFcfs().stream()
                .collect(Collectors.partitioningBy(it -> it.getId() == null, Collectors.toMap(FcfsEventDto::getId, it-> it)));
        // true이면 created / false이면 updated
        Map<Long, FcfsEventDto> createdDtos = fcfsAllDtos.get(true);
        Map<Long, FcfsEventDto> updatedDtos = fcfsAllDtos.get(false);

        Set<Long> updated = new HashSet<>(updatedDtos.keySet());
        Set<Long> deleted = fcfsEvents.stream().map(FcfsEvent::getId).collect(Collectors.toSet());
        // null은 created
        updated.retainAll(deleted); // dto & entity 교집합 => updated
        deleted.removeAll(updated); // entity에는 있는데 dto에는 없음 => deleted
        for(FcfsEvent event: fcfsEvents) {
            // update에 있는 경우
            if(updated.contains(event.getId())) {
                FcfsEventDto updatedDto = updatedDtos.get(event.getId());
                event.updateStartTime(updatedDto.getStartTime());
                event.updateEndTime(updatedDto.getEndTime());
                event.updateParticipantCount(updatedDto.getParticipantCount());
                event.updatePrizeInfo(updatedDto.getPrizeInfo());
            }
            // delete에 있는 경우
            else if(deleted.contains(event.getId())) {
                fcfsEventRepository.delete(event);
            }
        }
        fcfsEvents.removeIf(it -> deleted.contains(it.getId()));

        // 객체 생성 처리
        for(FcfsEventDto createdDto : createdDtos.values()) {
            FcfsEvent newEvent = FcfsEvent.of(
                    createdDto.getStartTime(),
                    createdDto.getEndTime(),
                    createdDto.getParticipantCount(),
                    createdDto.getPrizeInfo(),
                    metadata
            );
            fcfsEvents.add(newEvent);
        }
    }
}
