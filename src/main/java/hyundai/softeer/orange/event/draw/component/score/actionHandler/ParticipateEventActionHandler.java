package hyundai.softeer.orange.event.draw.component.score.actionHandler;

import hyundai.softeer.orange.event.draw.repository.EventParticipationInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component("ParticipateEvent_ActionHandler")
public class ParticipateEventActionHandler implements ActionHandler {
    private final EventParticipationInfoRepository repo;

    @Override
    public void handle(Map<Long, Long> scoreMap, long eventRawId, long score) {
        var participateCounts =  repo.countPerEventUserByEventId(eventRawId);
        for (var p : participateCounts) {
            long beforeScore = scoreMap.getOrDefault(p.getEventUserId(),0L);
            scoreMap.put(p.getEventUserId(), beforeScore + p.getCount() * score);
        }
    }
}
