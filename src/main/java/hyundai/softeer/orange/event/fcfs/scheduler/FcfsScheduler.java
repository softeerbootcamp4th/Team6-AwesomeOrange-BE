package hyundai.softeer.orange.event.fcfs.scheduler;

import hyundai.softeer.orange.event.fcfs.service.FcfsManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FcfsScheduler {

    private final FcfsManageService fcfsManageService;

    // 매일 자정 1분마다 실행되며, 오늘의 선착순 이벤트에 대한 정보를 DB에서 Redis로 이동시킨다.
    @Scheduled(cron = "0 1 0 * * *")
    public void registerFcfsEvents() {
        fcfsManageService.registerFcfsEvents();
    }

    // 매일 자정마다 실행되며, 선착순 이벤트 당첨자들을 Redis에서 DB로 이동시킨다.
    @Scheduled(cron = "0 0 0 * * *")
    public void registerWinners() {
        fcfsManageService.registerWinners();
    }
}
