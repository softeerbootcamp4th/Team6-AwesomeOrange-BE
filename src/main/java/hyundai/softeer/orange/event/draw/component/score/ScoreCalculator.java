package hyundai.softeer.orange.event.draw.component.score;

import hyundai.softeer.orange.event.draw.component.score.actionHandler.ActionHandler;
import hyundai.softeer.orange.event.draw.entity.DrawEventScorePolicy;
import hyundai.softeer.orange.event.draw.enums.DrawEventAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 특정 추첨 이벤트에 대한 점수를 계산하는 클래스. 저장은 안한다.
 */
@RequiredArgsConstructor
@Component
public class ScoreCalculator {
    private final Map<DrawEventAction, ActionHandler> handlerMap;

    public Map<Long, Long> calculate(long eventId, List<DrawEventScorePolicy> policies) {
        Map<Long, Long> scoreMap = new HashMap<>();
        for (var policy : policies) {
            ActionHandler handler = handlerMap.get(policy.getAction());
            handler.handle(scoreMap, eventId, policy.getScore());
        }
        return scoreMap;
    }
}
