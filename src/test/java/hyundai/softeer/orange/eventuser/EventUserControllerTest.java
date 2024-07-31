package hyundai.softeer.orange.eventuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.ErrorResponse;
import hyundai.softeer.orange.common.dto.TokenDto;
import hyundai.softeer.orange.common.util.MessageUtil;
import hyundai.softeer.orange.core.jwt.JWTManager;
import hyundai.softeer.orange.eventuser.controller.EventUserController;
import hyundai.softeer.orange.eventuser.dto.RequestAuthCodeDto;
import hyundai.softeer.orange.eventuser.dto.RequestUserDto;
import hyundai.softeer.orange.eventuser.exception.EventUserException;
import hyundai.softeer.orange.eventuser.service.EventUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventUserController.class)
class EventUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private EventUserService eventUserService;

    @MockBean
    private JWTManager jwtManager;

    ObjectMapper mapper = new ObjectMapper();
    RequestUserDto requestUserDto = new RequestUserDto("hyundai", "01000000000");
    TokenDto tokenDto = new TokenDto("token");

    @DisplayName("login: 로그인 API를 호출한다.")
    @ParameterizedTest(name = "name: {0}, phoneNumber: {1}")
    @CsvSource(value = {
            "박현대, 01011111111",
            "최기아, 01022222222",
            "김제네, 01033333333"
    })
    void loginTest(String name, String phoneNumber) throws Exception {
        // given
        requestUserDto = new RequestUserDto(name, phoneNumber);
        String requestBody = mapper.writeValueAsString(requestUserDto);
        String responseBody = mapper.writeValueAsString(tokenDto);
        when(eventUserService.login(any(RequestUserDto.class))).thenReturn(tokenDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event-user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("login: 로그인 API를 호출 시 RequestAuthUserDto의 유효성 검사가 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "010-0000-0000", "10011111111", "0101111222", "010111111111"})
    void login400Test(String phoneNumber) throws Exception {
        // given
        requestUserDto = new RequestUserDto(null, phoneNumber);
        String requestBody = mapper.writeValueAsString(requestUserDto);
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("name", MessageUtil.BAD_INPUT);
        expectedErrors.put("phoneNumber", MessageUtil.INVALID_PHONE_NUMBER);
        String responseBody = mapper.writeValueAsString(expectedErrors);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event-user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("login: 로그인 API를 호출 시 유저가 없어서 예외가 발생한다.")
    @Test
    void login404Test() throws Exception {
        // given
        String requestBody = mapper.writeValueAsString(requestUserDto);
        when(eventUserService.login(any(RequestUserDto.class))).thenThrow(new EventUserException(ErrorCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event-user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @DisplayName("sendAuthCode: 인증번호 전송 API를 호출한다.")
    @Test
    void sendAuthCodeTest() throws Exception {
        // given
        String requestBody = mapper.writeValueAsString(requestUserDto);
        doNothing().when(eventUserService).sendAuthCode(requestUserDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event-user/send-auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());
    }

    @DisplayName("checkAuthCode: 인증번호 검증 API를 호출한다.")
    @Test
    void checkAuthCodeTest() throws Exception {
        // given
        RequestAuthCodeDto requestAuthCodeDto = new RequestAuthCodeDto("name", "01000000000", "123456");
        String requestBody = mapper.writeValueAsString(requestAuthCodeDto);
        String responseBody = mapper.writeValueAsString(tokenDto);
        when(eventUserService.checkAuthCode(any(RequestAuthCodeDto.class))).thenReturn(tokenDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event-user/check-auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("checkAuthCode: 인증번호 검증 API를 호출 시 RequestAuthCodeDto의 인증번호의 유효성 검사가 실패한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "1234a", "1234a6", "1234567"})
    void checkAuthCode400Test(String code) throws Exception {
        // given
        RequestAuthCodeDto requestAuthCodeDto = new RequestAuthCodeDto("name", "01000000000", code);
        String requestBody = mapper.writeValueAsString(requestAuthCodeDto);
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("authCode", MessageUtil.INVALID_AUTH_CODE);
        String responseBody = mapper.writeValueAsString(expectedErrors);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event-user/check-auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseBody));
    }


    @DisplayName("checkAuthCode: 인증번호 검증 API를 호출 시 인증번호가 틀려서 예외가 발생한다.")
    @Test
    void checkAuthCode401Test() throws Exception {
        // given
        RequestAuthCodeDto requestAuthCodeDto = new RequestAuthCodeDto("name", "01000000000", "123456");
        String requestBody = mapper.writeValueAsString(requestAuthCodeDto);
        String responseBody = mapper.writeValueAsString(ErrorResponse.from(ErrorCode.INVALID_AUTH_CODE));
        when(eventUserService.checkAuthCode(any(RequestAuthCodeDto.class))).thenThrow(new EventUserException(ErrorCode.INVALID_AUTH_CODE));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event-user/check-auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json(responseBody));
    }
}
