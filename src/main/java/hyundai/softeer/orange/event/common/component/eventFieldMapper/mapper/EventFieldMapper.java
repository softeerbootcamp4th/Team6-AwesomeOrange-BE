package hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper;

import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.dto.EventDto;

public interface EventFieldMapper {
    boolean canHandle(EventType eventType);
    void fetchToEventEntity(EventMetadata metadata, EventDto dto);
    void fetchToDto(EventMetadata metadata, EventDto dto);
    void editEventField(EventMetadata metadata, EventDto dto);
}
