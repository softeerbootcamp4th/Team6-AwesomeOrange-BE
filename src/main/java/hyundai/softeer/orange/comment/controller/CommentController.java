package hyundai.softeer.orange.comment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hyundai.softeer.orange.comment.dto.CreateCommentDto;
import hyundai.softeer.orange.comment.dto.ResponseCommentsDto;
import hyundai.softeer.orange.comment.exception.CommentException;
import hyundai.softeer.orange.comment.service.CommentService;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.config.NaverApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Tag(name = "Comment", description = "기대평 관련 API")
@ConfigurationPropertiesScan
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@RestController
public class CommentController {

    private final CommentService commentService;
    private final NaverApiConfig naverApiConfig;

    @Tag(name = "Comment")
    @GetMapping
    @Operation(summary = "기대평 조회", description = "주기적으로 추출되는 긍정 기대평 목록을 조회한다.")
    public ResponseEntity<ResponseCommentsDto> getComments() {
        return ResponseEntity.ok(commentService.getComments());
    }

    @Tag(name = "Comment")
    @PostMapping
    @Operation(summary = "기대평 등록", description = "유저가 신규 기대평을 등록한다.", responses = {
            @ApiResponse(responseCode = "200", description = "기대평 등록 성공"),
            @ApiResponse(responseCode = "400", description = "기대평 등록 실패, 지나치게 부정적인 표현으로 간주될 때")
    })
    public ResponseEntity<Long> createComment(@RequestBody @Valid CreateCommentDto dto) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", naverApiConfig.getClientId());
        headers.set("X-NCP-APIGW-API-KEY", naverApiConfig.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(dto.getContent(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(naverApiConfig.getUrl(), requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        boolean isPositive = true;

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        String sentiment = rootNode.path("document").path("sentiment").asText();
        if (sentiment.equals("negative")) {
            isPositive = false;
            double documentNegativeConfidence = rootNode.path("document").path("confidence").path("negative").asDouble();
            if (documentNegativeConfidence >= 99.5) { // 부정이며 확률이 99.5% 이상일 경우 재작성 요청
                throw new CommentException(ErrorCode.INVALID_COMMENT);
            }
        }
        return ResponseEntity.ok(commentService.createComment(dto, isPositive));
    }
}
