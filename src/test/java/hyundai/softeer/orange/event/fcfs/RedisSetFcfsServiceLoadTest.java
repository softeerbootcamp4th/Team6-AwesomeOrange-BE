package hyundai.softeer.orange.event.fcfs;

import hyundai.softeer.orange.event.fcfs.service.RedisSetFcfsService;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class RedisSetFcfsServiceLoadTest {

    @Autowired
    private RedisSetFcfsService redisSetFcfsService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate<String, Integer> numberRedisTemplate;

    @Autowired
    RedisTemplate<String, Boolean> booleanRedisTemplate;

    Long eventSequence = 1L; // 테스트할 이벤트 시퀀스
    int numberOfWinners = 100; // 당첨자 수

    @BeforeEach
    void setUp() {
        // 초기화
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
        numberRedisTemplate.getConnectionFactory().getConnection().flushAll();
        booleanRedisTemplate.getConnectionFactory().getConnection().flushAll();

        // 테스트할 이벤트 정보 저장
        booleanRedisTemplate.opsForValue().set(FcfsUtil.endFlagFormatting(eventSequence.toString()), false);
        numberRedisTemplate.opsForValue().set(FcfsUtil.keyFormatting(eventSequence.toString()), numberOfWinners);
        stringRedisTemplate.opsForValue().set(FcfsUtil.startTimeFormatting(eventSequence.toString()), "2021-08-01T00:00:00");
    }

    @AfterEach
    void tearDown() {
        // 초기화
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
        numberRedisTemplate.getConnectionFactory().getConnection().flushAll();
        booleanRedisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void participateTest() throws InterruptedException {
        int numberOfThreads = 200; // 스레드 수
        int numberOfUsers = 1000; // 동시 참여 사용자 수

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        long startTime = System.currentTimeMillis();
        CountDownLatch latch = new CountDownLatch(numberOfUsers);
        for (int i = 0; i < numberOfUsers; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    boolean result = redisSetFcfsService.participate(1L, "user" + index);
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

        executorService.shutdown();
        Long count = stringRedisTemplate.opsForZSet().zCard(FcfsUtil.winnerFormatting(eventSequence.toString()));
        assertThat(count).isEqualTo(numberOfWinners);
    }
}
