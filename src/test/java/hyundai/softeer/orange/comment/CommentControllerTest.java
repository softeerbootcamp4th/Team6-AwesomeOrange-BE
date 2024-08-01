package hyundai.softeer.orange.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hyundai.softeer.orange.comment.controller.CommentController;
import hyundai.softeer.orange.comment.dto.CreateCommentDto;
import hyundai.softeer.orange.comment.dto.ResponseCommentDto;
import hyundai.softeer.orange.comment.dto.ResponseCommentsDto;
import hyundai.softeer.orange.comment.exception.CommentException;
import hyundai.softeer.orange.comment.service.ApiService;
import hyundai.softeer.orange.comment.service.CommentService;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.ErrorResponse;
import hyundai.softeer.orange.common.util.MessageUtil;
import hyundai.softeer.orange.core.jwt.JWTManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ApiService apiService;

    @MockBean
    private JWTManager jwtManager;

    ObjectMapper mapper = new ObjectMapper();
    CreateCommentDto createCommentDto = new CreateCommentDto(1L, 1L, "hello");
    String requestBody = "";

    @BeforeEach
    void setUp() throws JsonProcessingException {
        requestBody = mapper.writeValueAsString(createCommentDto);
    }

    @DisplayName("getComments: 기대평 조회 API를 호출한다.")
    @Test
    void getComments200Test() throws Exception {
        // given
        List<ResponseCommentDto> comments = List.of(
                ResponseCommentDto.builder().content("기대평1").build(),
                ResponseCommentDto.builder().content("기대평2").build()
        );
        ResponseCommentsDto responseCommentsDto = new ResponseCommentsDto(comments);
        when(commentService.getComments()).thenReturn(responseCommentsDto);
        String responseBody = mapper.writeValueAsString(responseCommentsDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/comment"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("getComments: 기대평 조회 API를 호출하며, 빈 목록을 반환한다.")
    @Test
    void getComments200EmptyTest() throws Exception {
        // given
        ResponseCommentsDto responseCommentsDto = new ResponseCommentsDto(List.of());
        when(commentService.getComments()).thenReturn(responseCommentsDto);
        String responseBody = mapper.writeValueAsString(responseCommentsDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/comment"))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("createComment: 기대평 등록 API를 호출한다.")
    @Test
    void createComment200Test() throws Exception {
        // given
        when(apiService.analyzeComment(createCommentDto.getContent())).thenReturn(true);
        when(commentService.createComment(any(CreateCommentDto.class), anyBoolean())).thenReturn(true);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true")); // Expect true
    }

    @DisplayName("createComment: 기대평 등록 API를 호출 시 기대평이 지나치게 부정적인 표현으로 간주되어 실패한다.")
    @Test
    void createComment400Test() throws Exception {
        // given
        when(apiService.analyzeComment(createCommentDto.getContent())).thenReturn(true);
        when(commentService.createComment(any(CreateCommentDto.class), anyBoolean()))
                .thenThrow(new CommentException(ErrorCode.INVALID_COMMENT));
        String responseBody = mapper.writeValueAsString(ErrorResponse.from(ErrorCode.INVALID_COMMENT));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("createComment: 기대평 등록 API를 호출 시 CreateCommentDto의 유효성 검사가 실패한다.")
    @Test
    void createComment400BadInputTest() throws Exception {
        // given
        CreateCommentDto badInput = new CreateCommentDto(null, null, "");
        requestBody = mapper.writeValueAsString(badInput);

        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put("eventUserId", MessageUtil.BAD_INPUT);
        expectedErrors.put("eventFrameId", MessageUtil.BAD_INPUT);
        expectedErrors.put("content", MessageUtil.OUT_OF_SIZE);
        String responseBody = mapper.writeValueAsString(expectedErrors);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("createComment: 기대평 등록 API를 호출 시 기대평 중복 작성으로 인해 실패한다.")
    @Test
    void createComment409Test() throws Exception {
        // given
        when(apiService.analyzeComment(createCommentDto.getContent())).thenReturn(true);
        when(commentService.createComment(any(CreateCommentDto.class), anyBoolean()))
                .thenThrow(new CommentException(ErrorCode.COMMENT_ALREADY_EXISTS));
        String responseBody = mapper.writeValueAsString(ErrorResponse.from(ErrorCode.COMMENT_ALREADY_EXISTS));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().json(responseBody));
    }
}
