package hyundai.softeer.orange.event.url.controller;

import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.event.url.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/api/v1/url/shorten")
    public ResponseEntity<String> urlShorten(@RequestParam String longUrl, @RequestParam String userId){
        // TODO: JWT 토큰으로부터 userId를 추출하여 사용하도록 추후 수정
        return ResponseEntity.ok(urlService.generateUrl(longUrl, userId));
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl){
        String longUrl = urlService.getLongUrl(shortUrl);
        return ResponseEntity.status(302).header(ConstantUtil.LOCATION, longUrl).build();
    }
}
