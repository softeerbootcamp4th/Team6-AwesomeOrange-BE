package hyundai.softeer.orange.event.fcfs;

import hyundai.softeer.orange.event.fcfs.service.RedisLockFcfsService;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import org.junit.jupiter.api.*;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@SpringBootTest
class RedisLockFcfsServiceLoadTest {

    @Autowired
    private RedisLockFcfsService redisLockFcfsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Boolean> booleanRedisTemplate;

    @Autowired
    private RedisTemplate<String, Integer> numberRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    Long eventSequence = 1L; // 테스트할 이벤트 시퀀스

    @BeforeEach
    void setUp() {
        // 필요한 경우 이전 데이터 삭제
        redissonClient.getKeys().flushdb();
        redissonClient.getKeys().flushall();

        // 테스트할 이벤트 정보 저장
        numberRedisTemplate.opsForValue().set(FcfsUtil.keyFormatting(eventSequence.toString()), 30);
        stringRedisTemplate.opsForValue().set(FcfsUtil.startTimeFormatting(eventSequence.toString()), "2021-08-01T00:00:00");
    }

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 데이터 삭제
        numberRedisTemplate.delete(FcfsUtil.keyFormatting(eventSequence.toString()));
        stringRedisTemplate.delete(FcfsUtil.startTimeFormatting(eventSequence.toString()));
        booleanRedisTemplate.delete(FcfsUtil.endFlagFormatting(eventSequence.toString()));
    }

    @DisplayName("부하 테스트: 1000명의 사용자가 동시에 참여를 시도한다.")
    @Test
    void loadTestParticipate() throws InterruptedException {
        int numberOfThreads = 200; // 스레드 수
        int numberOfUsers = 1000; // 동시 참여 사용자 수

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfUsers);
        String userIdPrefix = "user"; // 사용자 ID 접두사

        // 테스트 시작 시간
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfUsers; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    boolean result = redisLockFcfsService.participate(1L, userIdPrefix + index);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 완료될 때까지 대기
        latch.await();

        // 테스트 종료 시간
        long endTime = System.currentTimeMillis();
        log.info("Total time: {} ms", endTime - startTime);

        // 스레드 풀 종료
        executorService.shutdown();
    }
}
