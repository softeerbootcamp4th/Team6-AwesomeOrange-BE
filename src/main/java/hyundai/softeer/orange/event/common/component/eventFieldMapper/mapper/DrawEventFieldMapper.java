package hyundai.softeer.orange.event.common.component.eventFieldMapper.mapper;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.common.entity.EventMetadata;
import hyundai.softeer.orange.event.common.enums.EventType;
import hyundai.softeer.orange.event.common.exception.EventException;
import hyundai.softeer.orange.event.draw.entity.DrawEvent;
import hyundai.softeer.orange.event.draw.entity.DrawEventMetadata;
import hyundai.softeer.orange.event.draw.entity.DrawEventScorePolicy;
import hyundai.softeer.orange.event.draw.repository.DrawEventMetadataRepository;
import hyundai.softeer.orange.event.draw.repository.DrawEventScorePolicyRepository;
import hyundai.softeer.orange.event.dto.EventDto;
import hyundai.softeer.orange.event.dto.draw.DrawEventDto;
import hyundai.softeer.orange.event.dto.draw.DrawEventMetadataDto;
import hyundai.softeer.orange.event.dto.draw.DrawEventScorePolicyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * EventMetadata에 DrawEvent를 주입해주는 매퍼
 */
@RequiredArgsConstructor
@Component
public class DrawEventFieldMapper implements EventFieldMapper {
    private final DrawEventMetadataRepository deMetadataRepository;
    private final DrawEventScorePolicyRepository deScorePolicyRepository;

    @Override
    public boolean canHandle(EventType eventType) {
        return eventType.equals(EventType.draw);
    }

    @Override
    public void fetchToEventEntity(EventMetadata metadata, EventDto eventDto) {
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

    @Override
    public void fetchToDto(EventMetadata metadata, EventDto eventDto) {
        Optional<DrawEvent> drawEventOpt = metadata.getDrawEventList().stream().findFirst();
        // drawEvent 정보가 있으면 넣기
        drawEventOpt.ifPresent(drawEvent -> {
            DrawEventDto drawEventDto = DrawEventDto
                    .builder()
                    .id(drawEvent.getId())
                    .metadata(drawEvent.getMetadataList().stream().map(
                            it -> DrawEventMetadataDto.builder()
                                    .id(it.getId())
                                    .count(it.getCount())
                                    .grade(it.getGrade())
                                    .prizeInfo(it.getPrizeInfo())
                                    .build()
                    ).toList())
                    .policies(drawEvent.getPolicyList().stream().map(
                            it -> DrawEventScorePolicyDto.builder()
                                    .id(it.getId())
                                    .score(it.getScore())
                                    .action(it.getAction())
                                    .build()
                    ).toList())
                    .build();

            eventDto.setDraw(drawEventDto);
        });
    }

    @Override
    public void editEventField(EventMetadata metadata, EventDto dto) {
        Optional<DrawEvent> drawEventOpt = metadata.getDrawEventList().stream().findFirst();
        DrawEvent drawEvent = drawEventOpt.orElseThrow(() -> new EventException(ErrorCode.EVENT_NOT_FOUND));
        DrawEventDto drawEventDto = dto.getDraw();

        editDrawEventMetadata(drawEvent, drawEventDto);
        editDrawEventScorePolicy(drawEvent, drawEventDto);
    }

    // 나중에 가능하다면 리팩토링 방법 찾아보자. => set 기반 공통 로직 추출
    private void editDrawEventMetadata(DrawEvent drawEvent, DrawEventDto drawEventDto) {
        List<DrawEventMetadata> deMetadata = drawEvent.getMetadataList();

        Map<Boolean, Map<Long, DrawEventMetadataDto>> deMetadataDtos = drawEventDto.getMetadata().stream()
                .collect(Collectors.partitioningBy(it -> it.getId() == null, Collectors.toMap(DrawEventMetadataDto::getId, it-> it)));
        // true이면 created / false이면 updated
        Map<Long, DrawEventMetadataDto> createdDtos = deMetadataDtos.get(true);
        Map<Long, DrawEventMetadataDto> updatedDtos = deMetadataDtos.get(false);

        Set<Long> updated = new HashSet<>(updatedDtos.keySet());
        Set<Long> deleted = deMetadata.stream().map(DrawEventMetadata::getId).collect(Collectors.toSet());
        // null은 created
        updated.retainAll(deleted); // dto & entity 교집합 => updated
        deleted.removeAll(updated); // entity에는 있는데 dto에는 없음 => deleted

        for(DrawEventMetadata metadata : deMetadata) {
            // update에 있는 경우
            if(updated.contains(metadata.getId())) {
                DrawEventMetadataDto dto = updatedDtos.get(metadata.getId());
                metadata.updateCount(dto.getCount());
                metadata.updateGrade(dto.getGrade());
                metadata.updatePrizeInfo(dto.getPrizeInfo());
            }
            // delete에 있는 경우
            else if(deleted.contains(metadata.getId())) {
                deMetadataRepository.delete(metadata);
            }
        }
        deMetadata.removeIf(it -> deleted.contains(it.getId()));

        // 객체 생성 처리
        for(DrawEventMetadataDto createdDto : createdDtos.values()) {
            DrawEventMetadata drawEventMetadata = DrawEventMetadata.of(
                    createdDto.getGrade(),
                    createdDto.getCount(),
                    createdDto.getPrizeInfo(),
                    drawEvent
            );
            deMetadata.add(drawEventMetadata);
        }
    }

    private void editDrawEventScorePolicy(DrawEvent drawEvent, DrawEventDto drawEventDto) {
        List<DrawEventScorePolicy> policies = drawEvent.getPolicyList();

        Map<Boolean, Map<Long, DrawEventScorePolicyDto>> deMetadataDtos = drawEventDto.getPolicies().stream()
                .collect(Collectors.partitioningBy(it -> it.getId() == null, Collectors.toMap(DrawEventScorePolicyDto::getId, it-> it)));
        // true이면 created / false이면 updated
        Map<Long, DrawEventScorePolicyDto> createdDtos = deMetadataDtos.get(true);
        Map<Long, DrawEventScorePolicyDto> updatedDtos = deMetadataDtos.get(false);

        Set<Long> updated = new HashSet<>(updatedDtos.keySet());
        Set<Long> deleted = policies.stream().map(DrawEventScorePolicy::getId).collect(Collectors.toSet());
        // null은 created
        updated.retainAll(deleted); // dto & entity 교집합 => updated
        deleted.removeAll(updated); // entity에는 있는데 dto에는 없음 => deleted

        for(DrawEventScorePolicy policy : policies) {
            // update에 있는 경우
            if(updated.contains(policy.getId())) {
                DrawEventScorePolicyDto dto = updatedDtos.get(policy.getId());
                policy.updateAction(dto.getAction());
                policy.updateScore(dto.getScore());
            }
            // delete에 있는 경우
            else if(deleted.contains(policy.getId())) {
                deScorePolicyRepository.delete(policy);
            }
        }
        policies.removeIf(it -> deleted.contains(it.getId()));

        // 객체 생성 처리
        for(DrawEventScorePolicyDto createdDto : createdDtos.values()) {
            DrawEventScorePolicy policy = DrawEventScorePolicy.of(
                    createdDto.getAction(),
                    createdDto.getScore(),
                    drawEvent
            );
            policies.add(policy);
        }
    }

}
