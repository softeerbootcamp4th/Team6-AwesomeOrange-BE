package hyundai.softeer.orange.event.url.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.event.url.dto.ResponseUrlDto;
import hyundai.softeer.orange.event.url.entity.Url;
import hyundai.softeer.orange.event.url.exception.UrlException;
import hyundai.softeer.orange.event.url.repository.UrlRepository;
import hyundai.softeer.orange.event.url.util.UrlTypeValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class UrlService {

    private final UrlRepository urlRepository;

    @Transactional
    public ResponseUrlDto generateUrl(String originalUrl, String userId) {
        if(!UrlTypeValidation.isValidURL(originalUrl)){
            throw new UrlException(ErrorCode.INVALID_URL);
        }

        String shortUrl = generateShortUrl(userId);
        while(urlRepository.existsByShortUrl(shortUrl)){
            shortUrl = generateShortUrl(userId);
        }

        Url url = Url.of(originalUrl, shortUrl);
        urlRepository.save(url);
        return new ResponseUrlDto(shortUrl);
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String url) {
        Url shortUrl = urlRepository.findByShortUrl(url)
                .orElseThrow(() -> new UrlException(ErrorCode.SHORT_URL_NOT_FOUND));
        return shortUrl.getOriginalUrl();
    }

    private String generateShortUrl(String userId) {
        String characters = ConstantUtil.CHARACTERS;
        Random random = new Random();
        StringBuilder shortUrlsb = new StringBuilder(ConstantUtil.SHORT_URL_LENGTH);
        for (int i = 0; i < ConstantUtil.SHORT_URL_LENGTH; i++) {
            shortUrlsb.append(characters.charAt(random.nextInt(characters.length())));
        }
        // QueryString으로 userId를 추가
        shortUrlsb.append("?userId=").append(userId);
        return shortUrlsb.toString();
    }
}
