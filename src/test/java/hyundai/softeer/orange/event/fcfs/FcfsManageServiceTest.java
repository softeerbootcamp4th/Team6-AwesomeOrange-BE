package hyundai.softeer.orange.event.fcfs;

import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.event.fcfs.dto.ResponseFcfsWinnerDto;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEventWinningInfo;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventRepository;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventWinningInfoRepository;
import hyundai.softeer.orange.event.fcfs.service.FcfsManageService;
import hyundai.softeer.orange.event.fcfs.util.FcfsUtil;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FcfsManageServiceTest {

    @Autowired
    private FcfsManageService fcfsManageService;

    @Autowired
    private EventUserRepository eventUserRepository;

    @Autowired
    private FcfsEventRepository fcfsEventRepository;

    @Autowired
    private FcfsEventWinningInfoRepository fcfsEventWinningInfoRepository;

    @Autowired
    private EventFrameRepository eventFrameRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Integer> numberRedisTemplate;

    @Autowired
    private RedisTemplate<String, Boolean> booleanRedisTemplate;

    Long eventSequence;

    @BeforeEach
    void setUp() {
        // 초기화
        stringRedisTemplate.delete("*");
        numberRedisTemplate.delete("*");
        booleanRedisTemplate.delete("*");
        fcfsEventRepository.deleteAll();
        fcfsEventWinningInfoRepository.deleteAll();
        eventUserRepository.deleteAll();
        eventFrameRepository.deleteAll();

        EventFrame eventFrame = EventFrame.of("FcfsManageServiceTest");
        FcfsEvent fcfsEvent = FcfsEvent.builder()
                .startTime(LocalDateTime.now().plusSeconds(10))
                .participantCount(10L)
                .build();
        eventSequence = fcfsEventRepository.save(fcfsEvent).getId();
        eventFrameRepository.save(eventFrame);

        for(int i=0; i<5; i++){
            EventUser eventUser = EventUser.of("test"+i, "0101234567"+i, eventFrame, "uuid"+i);
            eventUserRepository.save(eventUser);
            fcfsEventWinningInfoRepository.save(FcfsEventWinningInfo.of(fcfsEvent, eventUser));
        }
    }

    @AfterEach
    void tearDown() {
        // 초기화
        stringRedisTemplate.delete("*");
        numberRedisTemplate.delete("*");
        booleanRedisTemplate.delete("*");
        fcfsEventWinningInfoRepository.deleteAll();
        fcfsEventRepository.deleteAll();
        eventUserRepository.deleteAll();
        eventFrameRepository.deleteAll();
    }

    @DisplayName("registerFcfsEvents: 오늘의 선착순 이벤트 정보(당첨자 수, 시작 시각)를 Redis에 배치")
    @Test
    void registerFcfsEvents() {
        // given
        List<FcfsEvent> events = fcfsEventRepository.findByStartTimeBetween(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        FcfsEvent fcfsEvent = events.get(0);

        // when
        fcfsManageService.registerFcfsEvents();

        // then
        Integer numberOfWinner = numberRedisTemplate.opsForValue().get(FcfsUtil.keyFormatting(fcfsEvent.getId().toString()));
        Boolean endFlag = booleanRedisTemplate.opsForValue().get(FcfsUtil.endFlagFormatting(fcfsEvent.getId().toString()));
        String startTime = stringRedisTemplate.opsForValue().get(FcfsUtil.startTimeFormatting(fcfsEvent.getId().toString()));
        String answer = stringRedisTemplate.opsForValue().get(FcfsUtil.answerFormatting(fcfsEvent.getId().toString()));

        assertThat(numberOfWinner).isEqualTo(fcfsEvent.getParticipantCount().intValue());
        assertThat(endFlag).isFalse();
        assertThat(startTime).isEqualTo(fcfsEvent.getStartTime().toString());
        assertThat(answer).isBetween("1", "5");
    }

    @DisplayName("registerWinners: redis에 저장된 모든 선착순 이벤트의 당첨자 정보를 DB로 이관")
    @Test
    void registerWinners() {
        // when
        fcfsManageService.registerFcfsEvents();
        for(int i=0; i<5; i++){
            stringRedisTemplate.opsForZSet().add(FcfsUtil.winnerFormatting("1"), "uuid"+i, i);
        }
        fcfsManageService.registerWinners();

        // then
        List<FcfsEventWinningInfo> infos = fcfsEventWinningInfoRepository.findByFcfsEventId(eventSequence);
        assertThat(infos).hasSize(5)
                .extracting("eventUser.userId")
                .contains("uuid0", "uuid1", "uuid2", "uuid3", "uuid4");
    }

    @DisplayName("getFcfsWinnersInfo: 특정 선착순 이벤트의 당첨자 조회 - 어드민에서 사용")
    @Test
    void getFcfsWinnersInfo() {
        // when
        List<ResponseFcfsWinnerDto> fcfsWinnersInfo = fcfsManageService.getFcfsWinnersInfo(eventSequence);

        // then
        assertThat(fcfsWinnersInfo)
                .hasSize(5)
                .extracting("name")
                .contains("test0", "test1", "test2", "test3", "test4");
    }
}
