package hyundai.softeer.orange.event.fcfs;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEvent;
import hyundai.softeer.orange.event.fcfs.entity.FcfsEventWinningInfo;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventRepository;
import hyundai.softeer.orange.event.fcfs.repository.FcfsEventWinningInfoRepository;
import hyundai.softeer.orange.event.fcfs.service.FcfsManageService;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class FcfsManageServiceUnitTest {

    @InjectMocks
    private FcfsManageService fcfsManageService;

    @Mock
    private EventUserRepository eventUserRepository;

    @Mock
    private FcfsEventRepository fcfsEventRepository;

    @Mock
    private FcfsEventWinningInfoRepository fcfsEventWinningInfoRepository;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ListOperations<String, String> valueOperations;

    EventFrame eventFrame = EventFrame.of("hyundai");
    List<FcfsEvent> events = List.of(
            FcfsEvent.of(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 10L, "prizeInfo", null));

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        given(stringRedisTemplate.opsForList()).willReturn(valueOperations);
    }

    @DisplayName("registerWinners: redis에 저장된 모든 선착순 이벤트의 당첨자 정보를 DB로 이관한다.")
    @Test
    void registerWinnersTest(){
        // given
        given(stringRedisTemplate.keys("*:fcfs")).willReturn(Set.of("1:fcfs"));
        given(stringRedisTemplate.opsForList().range("1", 0, -1)).willReturn(List.of("1"));
        given(fcfsEventRepository.findById(1L)).willReturn(Optional.of(events.get(0)));
        given(eventUserRepository.findAllById(List.of(1L, 2L))).willReturn(List.of(
                EventUser.of("test1", "01012345678", eventFrame, "1")));

        // when
        fcfsManageService.registerWinners();

        // then
        verify(fcfsEventWinningInfoRepository, times(1)).saveAll(anyList());
        verify(stringRedisTemplate, times(1)).delete("1:fcfs");
    }

    @DisplayName("registerWinners: 잘못된 eventId로 조회할 경우 예외를 발생시킨다.")
    @Test
    void registerWinnersNotFoundTest(){
        // given
        given(stringRedisTemplate.keys("*:fcfs")).willReturn(Set.of("1:fcfs"));
        given(stringRedisTemplate.opsForList().range("1", 0, -1)).willReturn(List.of("1"));
        given(fcfsEventRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> fcfsManageService.registerWinners())
                .isInstanceOf(FcfsEventException.class)
                .hasMessage(ErrorCode.FCFS_EVENT_NOT_FOUND.getMessage());
    }

    @DisplayName("getFcfsWinnersInfo: 특정 선착순 이벤트의 당첨자를 조회한다.")
    @Test
    void getFcfsWinnersInfoTest(){
        // given
        List<EventUser> eventUsers = List.of(
                EventUser.of("test1", "01012345678", eventFrame, "1"),
                EventUser.of("test2", "01000000001", eventFrame, "2"));
        List<FcfsEventWinningInfo> fcfsEventWinningInfos = List.of(
                FcfsEventWinningInfo.of(events.get(0), eventUsers.get(0)),
                FcfsEventWinningInfo.of(events.get(0), eventUsers.get(1)));
        given(fcfsEventRepository.findById(1L)).willReturn(java.util.Optional.ofNullable(events.get(0)));
        given(fcfsEventWinningInfoRepository.findByFcfsEventId(1L)).willReturn(fcfsEventWinningInfos);

        // when
        List<ResponseFcfsWinnerDto> fcfsWinnerDtos = fcfsManageService.getFcfsWinnersInfo(1L);

        // then
        assertThat(fcfsWinnerDtos).hasSize(2);
        assertThat(fcfsWinnerDtos.get(0).getName()).isEqualTo("test1");
        assertThat(fcfsWinnerDtos.get(0).getPhoneNumber()).isEqualTo("01012345678");
        assertThat(fcfsWinnerDtos.get(1).getName()).isEqualTo("test2");
        assertThat(fcfsWinnerDtos.get(1).getPhoneNumber()).isEqualTo("01000000001");
    }
}
