package hyundai.softeer.orange.event.url.controller;

import hyundai.softeer.orange.common.ErrorResponse;
import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.event.url.dto.ResponseUrlDto;
import hyundai.softeer.orange.event.url.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Url", description = "단축 URL 관련 API")
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@RestController
public class UrlController {

    private final UrlService urlService;

    @Tag(name = "Url")
    @PostMapping("/shorten")
    @Operation(summary = "URL 단축", description = "URL을 단축하여 반환합니다.", responses = {
            @ApiResponse(responseCode = "200", description = "URL 단축 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "URL 형식이 잘못되었을 때",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "유저를 찾지 못했을 때",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ResponseUrlDto> urlShorten(@RequestParam String originalUrl, @RequestParam String userId){
        // TODO: JWT 토큰으로부터 userId를 추출하여 사용하도록 추후 수정
        return ResponseEntity.ok(urlService.generateUrl(originalUrl, userId));
    }

    @Tag(name = "Url")
    @GetMapping("/{shortUrl}")
    @Operation(summary = "URL 리다이렉트", description = "단축 URL을 원본 URL로 리다이렉트합니다.", responses = {
            @ApiResponse(responseCode = "302", description = "URL 리다이렉트"),
            @ApiResponse(responseCode = "404", description = "단축 URL을 찾지 못했을 때",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> redirectToOriginalUrl(@PathVariable String shortUrl){
        return ResponseEntity.status(302)
                .header(ConstantUtil.LOCATION, urlService.getOriginalUrl(shortUrl))
                .build();
    }
}
