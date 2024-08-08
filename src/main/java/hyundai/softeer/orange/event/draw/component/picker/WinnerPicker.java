package hyundai.softeer.orange.event.draw.component.picker;

import java.util.List;

/**
 * "추첨" 작업을 진행하는 서비스. 여러 추첨 방식이 존재할 수 있으므로, 추첨 과정은 별도 인터페이스로 분리.
 */
public interface WinnerPicker {
    List<PickTarget> pick(List<PickTarget> items,long count);
}
