package hyundai.softeer.orange.event.url;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.event.url.dto.ResponseUrlDto;
import hyundai.softeer.orange.event.url.entity.Url;
import hyundai.softeer.orange.event.url.exception.UrlException;
import hyundai.softeer.orange.event.url.repository.UrlRepository;
import hyundai.softeer.orange.event.url.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class UrlServiceTest {

    @InjectMocks
    private UrlService urlService;

    @Mock
    private UrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    String originalUrl = "https://www.naver.com";
    String userId = "test";
    String shortUrl = "https://www.naver.com?userId=test";

    @DisplayName("generateUrl: 단축 URL을 생성한다.")
    @Test
    void generateUrlTest() {
        // given
        given(urlRepository.existsByShortUrl(shortUrl)).willReturn(false);

        // when
        ResponseUrlDto result = urlService.generateUrl(originalUrl);

        // then
        assertThat(result.getShortUrl()).isNotNull();
        verify(urlRepository).save(any());
    }

    @DisplayName("generateUrl: 단축 URL을 생성할 때 원본 URL이 유효하지 않으면 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "      ", "htppss://naver.com", "ww.naver.com", "navercom"})
    void generateUrlBadInputTest(String originalUrl) {
        // when & then
        assertThatThrownBy(() -> urlService.generateUrl(originalUrl))
                .isInstanceOf(UrlException.class)
                .hasMessage(ErrorCode.INVALID_URL.getMessage());
    }

    @DisplayName("getOriginalUrl: 단축 URL에 해당하는 원본 URL을 조회한다.")
    @Test
    void getOriginalUrlTest() {
        // given
        given(urlRepository.findByShortUrl(shortUrl)).willReturn(java.util.Optional.of(Url.of(originalUrl, shortUrl)));

        // when
        String result = urlService.getOriginalUrl(shortUrl);

        // then
        assertThat(result).isEqualTo(originalUrl);
    }

    @DisplayName("getOriginalUrl: 단축 URL에 해당하는 원본 URL이 없으면 예외를 발생시킨다.")
    @Test
    void getOriginalUrlNotFoundTest() {
        // given
        given(urlRepository.findByShortUrl(shortUrl)).willReturn(java.util.Optional.empty());

        // when & then
        assertThatThrownBy(() -> urlService.getOriginalUrl(shortUrl))
                .isInstanceOf(UrlException.class)
                .hasMessage(ErrorCode.SHORT_URL_NOT_FOUND.getMessage());
    }
}
