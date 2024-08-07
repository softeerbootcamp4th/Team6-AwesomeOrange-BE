package hyundai.softeer.orange.eventuser;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.dto.TokenDto;
import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.core.jwt.JWTManager;
import hyundai.softeer.orange.event.common.entity.EventFrame;
import hyundai.softeer.orange.event.common.repository.EventFrameRepository;
import hyundai.softeer.orange.eventuser.dto.RequestAuthCodeDto;
import hyundai.softeer.orange.eventuser.dto.RequestUserDto;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.exception.EventUserException;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import hyundai.softeer.orange.eventuser.service.EventUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

class EventUserServiceTest {

    @InjectMocks
    private EventUserService eventUserService;

    @Mock
    private EventUserRepository eventUserRepository;

    @Mock
    private EventFrameRepository eventFrameRepository;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private JWTManager jwtManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
    }

    RequestUserDto requestUserDto = RequestUserDto.builder()
            .name("test")
            .phoneNumber("01012345678")
            .build();
    TokenDto tokenDto = new TokenDto("token");
    EventFrame eventFrame = EventFrame.of("eventFrame");
    EventUser eventUser = EventUser.of("test", "01000000000", eventFrame, "uuid");
    Long eventFrameId = 1L;

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

    @DisplayName("checkAuthCode: 유저가 전송한 인증번호를 Redis 상에서 확인하고 성공한다.")
    @ParameterizedTest
    @ValueSource(strings = {"123456", "654321", "111111", "921345"})
    void checkAuthCodeTest(String authCode) {
        // given
        given(stringRedisTemplate.opsForValue().get(eventUser.getPhoneNumber())).willReturn(authCode);
        given(eventFrameRepository.findById(any())).willReturn(Optional.of(eventFrame));
        given(jwtManager.generateToken(anyString(), anyMap(), eq(ConstantUtil.JWT_LIFESPAN)))
                .willReturn(tokenDto.token());
        RequestAuthCodeDto requestAuthCodeDto = RequestAuthCodeDto.builder()
                .name(eventUser.getUserName())
                .phoneNumber(eventUser.getPhoneNumber())
                .authCode(authCode)
                .build();

        // when
        TokenDto result = eventUserService.checkAuthCode(requestAuthCodeDto, eventFrameId);

        // then
        assertThat(result.token()).isEqualTo(tokenDto.token());
    }

    @DisplayName("checkAuthCode: 유저가 전송한 인증번호를 Redis 상에서 확인하고 실패한다.")
    @ParameterizedTest(name = "authCode: {0}, requestAuthCode: {1}")
    @CsvSource({
            "123456, 1234567",
            "654321, 6543210",
            "111111, 1111111",
            "921345, 9213450"
    })
    void checkAuthCodeFailTest(String authCode, String requestAuthCode) {
        // given
        given(stringRedisTemplate.opsForValue().get(requestUserDto.getPhoneNumber())).willReturn(authCode);
        RequestAuthCodeDto requestAuthCodeDto = RequestAuthCodeDto.builder()
                .phoneNumber(requestUserDto.getPhoneNumber())
                .authCode(requestAuthCode)
                .build();

        // when & then
        assertThatThrownBy(() -> eventUserService.checkAuthCode(requestAuthCodeDto, eventFrameId))
                .isInstanceOf(EventUserException.class)
                .hasMessage(ErrorCode.INVALID_AUTH_CODE.getMessage());
    }

    @DisplayName("checkAuthCode: Redis에 저장된 인증번호가 없어 예외가 발생한다.")
    @Test
    void checkAuthCodeBadRequestTest() {
        // given
        given(stringRedisTemplate.opsForValue().get(requestUserDto.getPhoneNumber())).willReturn(null);
        RequestAuthCodeDto requestAuthCodeDto = RequestAuthCodeDto.builder()
                .phoneNumber(requestUserDto.getPhoneNumber())
                .authCode("123456")
                .build();

        // when & then
        assertThatThrownBy(() -> eventUserService.checkAuthCode(requestAuthCodeDto, eventFrameId))
                .isInstanceOf(EventUserException.class)
                .hasMessage(ErrorCode.BAD_REQUEST.getMessage());
    }
}
