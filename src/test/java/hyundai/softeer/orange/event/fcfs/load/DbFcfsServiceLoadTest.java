package hyundai.softeer.orange.event.fcfs.load;

import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventRepository;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventWinningInfoRepository;
import hyundai.softeer.orange.event.fcfs.service.DbFcfsService;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class DbFcfsServiceLoadTest {

    @Autowired
    private DbFcfsService dbFcfsService;

    @Autowired
    private FcfsEventRepository fcfsEventRepository;

    @Autowired
    private EventUserRepository eventUserRepository;

    @Autowired
    private FcfsEventWinningInfoRepository fcfsEventWinningInfoRepository;

    @Autowired
    private RedisTemplate<String, Boolean> booleanRedisTemplate;

    Long numberOfWinners = 100L;
    int numberOfThreads = 200; // 스레드 수
    int numberOfUsers = 1000; // 동시 참여 사용자 수

    @BeforeEach
    void setUp() {
        // 초기화
        fcfsEventWinningInfoRepository.deleteAll();
        eventUserRepository.deleteAll();
        fcfsEventRepository.deleteAll();
        booleanRedisTemplate.getConnectionFactory().getConnection().flushAll();

        // 이벤트 생성
        FcfsEvent fcfsEvent = FcfsEvent.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1), numberOfWinners, "prizeInfo", null);
        fcfsEventRepository.save(fcfsEvent);

        // 유저 생성
        for (int i = 0; i < numberOfUsers; i++) {
            eventUserRepository.save(EventUser.of("user", "phone" + i, null, "user"+i));
        }
    }

    @AfterEach
    void tearDown() {
        fcfsEventWinningInfoRepository.deleteAll();
        eventUserRepository.deleteAll();
        fcfsEventRepository.deleteAll();
    }

    @Test
    void participateTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(numberOfUsers);
        for (int i = 0; i < numberOfUsers; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    boolean result = dbFcfsService.participate(1L, "user" + index);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        long endTime = System.currentTimeMillis();
        log.info("Total time: {} ms", endTime - startTime);
        assertThat(fcfsEventWinningInfoRepository.count()).isEqualTo(numberOfWinners);
        executorService.shutdown();
    }
}
