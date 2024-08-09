package hyundai.softeer.orange.event.draw.enums;

/**
 * 점수 계산에 반영될 수 있는 작업 목록. 대응되는 handler이 등록되어야 한다.
 *
 * <p>ActionHandler: {@link hyundai.softeer.orange.event.draw.component.score.actionHandler.ActionHandler}</p>
 */
public enum DrawEventAction {
    WriteComment,
    ParticipateEvent
}
