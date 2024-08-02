package hyundai.softeer.orange.event.url;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.ErrorResponse;
import hyundai.softeer.orange.core.jwt.JWTManager;
import hyundai.softeer.orange.event.url.controller.UrlController;
import hyundai.softeer.orange.event.url.dto.ResponseUrlDto;
import hyundai.softeer.orange.event.url.exception.UrlException;
import hyundai.softeer.orange.event.url.service.UrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private JWTManager jwtManager;

    @MockBean
    private UrlService urlService;

    ObjectMapper mapper = new ObjectMapper();
    String originalUrl = "https://www.google.com";
    String shortUrl = "shortUrl";
    String userId = "test";

    @DisplayName("urlShorten: originalUrl과 userId를 전달받아 ResponseUrlDto를 반환한다.")
    @Test
    void urlShortenTest() throws Exception {
        // given
        when(urlService.generateUrl(originalUrl, userId)).thenReturn(new ResponseUrlDto("shortUrl"));
        String responseBody = mapper.writeValueAsString(new ResponseUrlDto("shortUrl"));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/url/shorten")
                .param("originalUrl", originalUrl)
                .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("urlShorten: originalUrl이 유효하지 않은 경우 ErrorResponse를 반환한다.")
    @Test
    void urlShorten400Test() throws Exception {
        // given
        when(urlService.generateUrl(originalUrl, userId)).thenThrow(new UrlException(ErrorCode.INVALID_URL));
        String responseBody = mapper.writeValueAsString(ErrorResponse.from(ErrorCode.INVALID_URL));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/url/shorten")
                .param("originalUrl", originalUrl)
                .param("userId", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("urlShorten: userId가 존재하지 않는 경우 ErrorResponse를 반환한다.")
    @Test
    void urlShorten404Test() throws Exception {
        // given
        when(urlService.generateUrl(originalUrl, userId)).thenThrow(new UrlException(ErrorCode.USER_NOT_FOUND));
        String responseBody = mapper.writeValueAsString(ErrorResponse.from(ErrorCode.USER_NOT_FOUND));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/url/shorten")
                .param("originalUrl", originalUrl)
                .param("userId", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().json(responseBody));
    }

    @DisplayName("redirectToOriginalUrl: shortUrl을 전달받아 원본 URL로 리다이렉트한다.")
    @Test
    void redirectToOriginalUrlTest() throws Exception {
        // given
        when(urlService.getOriginalUrl(shortUrl)).thenReturn("https://www.google.com");

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/url/" + shortUrl))
                .andExpect(status().isFound());
    }

    @DisplayName("redirectToOriginalUrl: shortUrl이 존재하지 않는 경우 예외가 발생해야 한다.")
    @Test
    void redirectToOriginalUrl404Test() throws Exception {
        // given
        when(urlService.getOriginalUrl(shortUrl)).thenThrow(new UrlException(ErrorCode.SHORT_URL_NOT_FOUND));
        String responseBody = mapper.writeValueAsString(ErrorResponse.from(ErrorCode.SHORT_URL_NOT_FOUND));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/url/" + shortUrl))
                .andExpect(status().isNotFound())
                .andExpect(content().json(responseBody));
    }
}
