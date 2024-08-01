package hyundai.softeer.orange.event.url.service;

import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.event.url.dto.ResponseUrlDto;
import hyundai.softeer.orange.event.url.entity.Url;
import hyundai.softeer.orange.event.url.exception.UrlException;
import hyundai.softeer.orange.event.url.repository.UrlRepository;
import hyundai.softeer.orange.event.url.util.UrlTypeValidation;
import hyundai.softeer.orange.eventuser.entity.EventUser;
import hyundai.softeer.orange.eventuser.repository.EventUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@RequiredArgsConstructor
@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final EventUserRepository eventUserRepository;

    @Transactional
    public ResponseUrlDto generateUrl(String originalUrl, String userId) {
        if(!UrlTypeValidation.valid(originalUrl)){
            throw new UrlException(ErrorCode.INVALID_URL);
        }
        EventUser eventUser = eventUserRepository.findByUserId(userId)
                .orElseThrow(() -> new UrlException(ErrorCode.EVENT_USER_NOT_FOUND));

        String shortUrl = generateShortUrl();
        while(urlRepository.existsByShortUrl(shortUrl)){
            shortUrl = generateShortUrl();
        }

        Url url = Url.of(originalUrl, shortUrl, eventUser);
        return new ResponseUrlDto(urlRepository.save(url).getShortUrl());
    }

    @Transactional(readOnly = true)
    public String getOriginalUrl(String url) {
        Url shortUrl = urlRepository.findByShortUrl(url)
                .orElseThrow(() -> new UrlException(ErrorCode.SHORT_URL_NOT_FOUND));
        return shortUrl.getOriginalUrl();
    }

    private String generateShortUrl() {
        String characters = ConstantUtil.CHARACTERS;
        Random random = new Random();
        StringBuilder shortUrlsb = new StringBuilder(ConstantUtil.SHORT_URL_LENGTH);
        for (int i = 0; i < ConstantUtil.SHORT_URL_LENGTH; i++) {
            shortUrlsb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return shortUrlsb.toString();
    }
}
