package hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.common.exception.EventException;
import hyundai.softeer.orange.event.dto.EventDto;
import hyundai.softeer.orange.event.dto.fcfs.FcfsEventDto;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * EventMetadata에 FcfsEvent를 주입해주는 매퍼
 */
@Component
public class FcfsEventFieldMapper implements EventFieldMapper {
    @Override
    public boolean canHandle(EventType eventType) {
        return eventType.equals(EventType.fcfs);
    }

    @Override
    public void handle(EventMetadata metadata, EventDto eventDto) {
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
}
