package hyundai.softeer.orange.event.draw.component.score.actionHandler;

import hyundai.softeer.orange.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component("WriteComment_ActionHandler")
public class WriteCommentActionHandler implements ActionHandler {
    private final CommentRepository repo;

    @Override
    public void handle(Map<Long, Long> scoreMap, long eventRawId, long score) {
        var commentCounts = repo.countPerEventUserByEventId(eventRawId);
        for (var c : commentCounts) {
            long beforeScore = scoreMap.getOrDefault(c.getEventUserId(), 0L);
            scoreMap.put(c.getEventUserId(), beforeScore + c.getCount() * score);
        }
    }
}
