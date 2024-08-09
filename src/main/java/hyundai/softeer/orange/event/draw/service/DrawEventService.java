package hyundai.softeer.orange.event.draw.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.draw.component.picker.PickTarget;
import hyundai.softeer.orange.event.draw.component.score.ScoreCalculator;
import hyundai.softeer.orange.event.draw.dto.DrawEventWinningInfoBulkInsertDto;
import hyundai.softeer.orange.event.draw.entity.DrawEvent;
import hyundai.softeer.orange.event.draw.entity.DrawEventMetadata;
import hyundai.softeer.orange.event.draw.entity.DrawEventScorePolicy;
import hyundai.softeer.orange.event.draw.exception.DrawEventException;
import hyundai.softeer.orange.event.draw.repository.DrawEventRepository;
import hyundai.softeer.orange.event.draw.component.picker.WinnerPicker;
import hyundai.softeer.orange.event.draw.repository.DrawEventWinningInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 추첨 이벤트를 다루는 서비스
 */
@RequiredArgsConstructor
@Service
public class DrawEventService {
    private final DrawEventRepository deRepository;
    private final DrawEventWinningInfoRepository deWinningInfoRepository;
    private final WinnerPicker picker;
    private final ScoreCalculator calculator;

    /**
     * eventId에 대한 추첨을 진행하는 메서드
     * @param eventId 이벤트의 id 값
     */
    @Transactional
    @Async
    public void draw(String eventId) {
        // 채점 & 추첨 과정 분리하는 것도 좋을것 같다.
        DrawEvent drawEvent = deRepository.findByEventId(eventId)
                .orElseThrow(() -> new DrawEventException(ErrorCode.EVENT_NOT_FOUND));

        // draw event의 primary key id 값
        long drawEventRawId = drawEvent.getId();

        // 점수 계산. 추후 추첨 과정과 분리될 수도 있음.
        List<DrawEventScorePolicy> policies = drawEvent.getPolicyList();
        var userScoreMap = calculator.calculate(drawEventRawId, policies);

        // 추첨 타겟 리스트 생성
        List<PickTarget> targets = userScoreMap.entrySet().stream()
                .map(it -> new PickTarget(it.getKey(), it.getValue())).toList();

        // 몇 등이 몇명이나 있는지 적혀 있는 정보. 등급끼리 정렬해서 1 ~ n 등 순서로 정렬
        List<DrawEventMetadata> metadataList = drawEvent.getMetadataList();
        metadataList.sort(Comparator.comparing(DrawEventMetadata::getGrade));

        // 총 당첨 인원 설정
        long pickCount = drawEvent.getMetadataList().stream().mapToLong(DrawEventMetadata::getCount).sum();

        // 당첨된 인원 구하기
        var pickedTargets = picker.pick(targets, pickCount);

        // 인원 등록을 위한 작업
        List<DrawEventWinningInfoBulkInsertDto> insertTargets = new ArrayList<>();
        int mdIdx = -1;
        long remain = 0;
        long grade = -1;
        DrawEventMetadata metadata = null;

        for(var target : pickedTargets) {
            if(remain <= 0) {
                mdIdx++;
                metadata = metadataList.get(mdIdx);
                grade = metadata.getGrade();
                remain = metadata.getCount();
            }

            insertTargets.add(DrawEventWinningInfoBulkInsertDto.of(
                    target.key(),
                    grade,
                    drawEventRawId
            ));
        }

        deWinningInfoRepository.insertMany(insertTargets);
    }
}
