package hyundai.softeer.orange.eventuser;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.dto.TokenDto;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.eventuser.dto.RequestAuthCodeDto;
import hyundai.softeer.orange.eventuser.dto.RequestUserDto;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.exception.EventUserException;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import hyundai.softeer.orange.eventuser.service.EventUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class EventUserServiceTest {

    @InjectMocks
    private EventUserService eventUserService;

    @Mock
    private EventUserRepository eventUserRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    RequestUserDto requestUserDto = RequestUserDto.builder()
            .name("test")
            .phoneNumber("01012345678")
            .build();
    TokenDto tokenDto = new TokenDto("token");
    EventFrame eventFrame = EventFrame.of("eventFrame");
    EventUser eventUser = EventUser.of("test", "01000000000", eventFrame, "uuid");

    @DisplayName("login: 유저가 로그인한다.")
    @Test
    void loginTest() {
        // given
        given(eventUserRepository.findByUserNameAndPhoneNumber(requestUserDto.getName(), requestUserDto.getPhoneNumber()))
                .willReturn(Optional.of(eventUser));

        // when
        TokenDto result = eventUserService.login(requestUserDto);

        // then
        assertThat(result).isNotNull();
    }

    @DisplayName("login: 유저가 로그인 시 유저를 찾을 수 없어 예외가 발생한다.")
    @Test
    void loginNotFoundTest() {
        // given
        given(eventUserRepository.findByUserNameAndPhoneNumber(requestUserDto.getName(), requestUserDto.getPhoneNumber()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> eventUserService.login(requestUserDto))
                .isInstanceOf(EventUserException.class)
                .hasMessage(ErrorCode.EVENT_USER_NOT_FOUND.getMessage());
    }

    @DisplayName("sendAuthCode: 유저에게 외부 API를 통해 인증번호를 전송하고, Redis에 유저의 전화번호-인증번호를 저장한다.")
    @Test
    void sendAuthCodeTest() {
        // given
        given(redisTemplate.opsForValue().get(requestUserDto.getPhoneNumber())).willReturn("123456");

        // when

        // then
    }

    @DisplayName("checkAuthCode: 유저가 전송한 인증번호를 Redis 상에서 확인하고 성공한다.")
    @Test
    void checkAuthCodeTest() {
        // given
        given(redisTemplate.opsForValue().get(requestUserDto.getPhoneNumber())).willReturn("123456");
        RequestAuthCodeDto requestAuthCodeDto = RequestAuthCodeDto.builder()
                .phoneNumber(requestUserDto.getPhoneNumber())
                .authCode("123456")
                .build();

        // when
        TokenDto result = eventUserService.checkAuthCode(requestAuthCodeDto);

        // then
        assertThat(result).isNotNull();
    }

    @DisplayName("checkAuthCode: 유저가 전송한 인증번호를 Redis 상에서 확인하고 실패한다.")
    @Test
    void checkAuthCodeFailTest() {
        // given
        given(redisTemplate.opsForValue().get(requestUserDto.getPhoneNumber())).willReturn("123");
        RequestAuthCodeDto requestAuthCodeDto = RequestAuthCodeDto.builder()
                .phoneNumber(requestUserDto.getPhoneNumber())
                .authCode("123456")
                .build();

        // when
        assertThatThrownBy(() -> eventUserService.checkAuthCode(requestAuthCodeDto))
                .isInstanceOf(EventUserException.class)
                .hasMessage(ErrorCode.INVALID_AUTH_CODE.getMessage());
    }
}
