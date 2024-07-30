package hyundai.softeer.orange.comment.scheduler;

import hyundai.softeer.orange.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentScheduler {

    private final CommentService commentService;

    // 스케줄러에 의해 일정 시간마다 캐싱된 긍정 기대평 목록을 초기화한다.
    @Scheduled(fixedRate = 720000) // 2시간마다 실행
    private void clearCache() {
        commentService.getComments();
    }
}
