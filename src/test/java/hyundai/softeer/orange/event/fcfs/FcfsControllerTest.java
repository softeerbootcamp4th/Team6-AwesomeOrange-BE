package hyundai.softeer.orange.event.fcfs;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.ErrorResponse;
import hyundai.softeer.orange.core.jwt.JWTManager;
import hyundai.softeer.orange.event.fcfs.controller.FcfsController;
import hyundai.softeer.orange.event.fcfs.exception.FcfsEventException;
import hyundai.softeer.orange.event.fcfs.service.FcfsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FcfsController.class)
class FcfsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private FcfsService fcfsService;

    @MockBean
    private JWTManager jwtManager;

    ObjectMapper mapper = new ObjectMapper();

    @DisplayName("participate: 선착순 이벤트 참여 성공 및 실패")
    @ParameterizedTest(name = "flag: {0}")
    @ValueSource(booleans = {true, false})
    void participateTest(boolean flag) throws Exception {
        // given
        when(fcfsService.participate(1L, "hyundai")).thenReturn(flag);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event/fcfs")
                .param("eventSequence", "1")
                .param("userId", "hyundai"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(flag)));
    }

    @DisplayName("participate: 선착순 이벤트 참여 시 이벤트 시간이 아니어서 예외가 발생하는 경우")
    @Test
    void participate400Test() throws Exception {
        // given
        when(fcfsService.participate(1L, "hyundai")).thenThrow(new FcfsEventException(ErrorCode.INVALID_EVENT_TIME));
        String responseBody = mapper.writeValueAsString(ErrorResponse.from(ErrorCode.INVALID_EVENT_TIME));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event/fcfs")
                .param("eventSequence", "1")
                .param("userId", "hyundai"))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("participate: 선착순 이벤트 참여 시 요청 형식이 잘못된 경우")
    @ParameterizedTest(name = "eventSequence: {0}")
    @ValueSource(strings = {"", " ", "a", "1.1", "1.0", "1.1.1", ""})
    void participateBadInputTest(String eventSequence) throws Exception {
        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/event/fcfs")
                .param("eventSequence", eventSequence)
                .param("userId", "hyundai"))
                .andExpect(status().isBadRequest());
    }
}
