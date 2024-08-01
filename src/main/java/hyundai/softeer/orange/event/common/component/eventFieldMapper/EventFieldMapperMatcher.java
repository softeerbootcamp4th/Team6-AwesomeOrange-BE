package hyundai.softeer.orange.event.common.component.eventFieldMapper;

import hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper.EventFieldMapper;
import hyundai.softeer.orange.event.common.enums.EventType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EventFieldMapperMatcher {
    private final List<EventFieldMapper> mappers;

    public EventFieldMapper getMapper(EventType eventType) {
        for (EventFieldMapper mapper : mappers) {
            if(mapper.canHandle(eventType)) return mapper;
        }

        return null;
    }
}
