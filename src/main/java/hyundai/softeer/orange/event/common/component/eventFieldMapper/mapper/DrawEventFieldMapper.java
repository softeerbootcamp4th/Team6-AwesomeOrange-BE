package hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.common.exception.EventException;
import hyundai.softeer.orange.event.draw.entity.DrawEvent;
import hyundai.softeer.orange.event.draw.entity.DrawEventMetadata;
import hyundai.softeer.orange.event.draw.entity.DrawEventScorePolicy;
import hyundai.softeer.orange.event.dto.EventDto;
import hyundai.softeer.orange.event.dto.draw.DrawEventDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * EventMetadata에 DrawEvent를 주입해주는 매퍼
 */
@Component
public class DrawEventFieldMapper implements EventFieldMapper {
    @Override
    public boolean canHandle(EventType eventType) {
        return eventType.equals(EventType.draw);
    }

    @Override
    public void handle(EventMetadata metadata, EventDto eventDto) {
        DrawEventDto dto = eventDto.getDraw();
        // 비어 있으면 안됨.
        if (dto == null) throw new EventException(ErrorCode.INVALID_JSON);

        DrawEvent event = new DrawEvent();
        metadata.addDrawEvent(event);
        event.setEventMetadata(metadata);

        List<DrawEventScorePolicy> policies = dto.getPolicies().stream().map(
                it -> DrawEventScorePolicy.of(
                        it.getAction(),
                        it.getScore(),
                        event
                )
        ).toList();
        event.setPolicyList(policies);

        List<DrawEventMetadata> metadataList = dto.getMetadata().stream().map(
                it -> DrawEventMetadata.of(
                        it.getGrade(),
                        it.getCount(),
                        it.getPrizeInfo(),
                        event
                )
        ).toList();
        event.setMetadataList(metadataList);
    }
}
