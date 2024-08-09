package hyundai.softeer.orange.event.draw.component.score.actionHandler;

import java.util.Map;

/**
 * 채점 정책에 대한 동작을 처리한다.
 * <p>component 이름은 action 이름 + ActionHandler 형식을 띄어야 한다.</p>
 * <p>액션 = {@link hyundai.softeer.orange.event.draw.enums.DrawEventAction}</p>
 */
public interface ActionHandler {
    void handle(Map<Long, Long> scoreMap, long eventRawId, long score);
}
