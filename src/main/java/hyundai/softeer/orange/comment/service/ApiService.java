package hyundai.softeer.orange.comment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hyundai.softeer.orange.comment.exception.CommentException;
import hyundai.softeer.orange.common.ErrorCode;
import hyundai.softeer.orange.common.util.ConstantUtil;
import hyundai.softeer.orange.config.NaverApiConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
public class ApiService {

    private final NaverApiConfig naverApiConfig;

    public boolean analyzeComment(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ConstantUtil.CLIENT_ID, naverApiConfig.getClientId());
        headers.set(ConstantUtil.CLIENT_SECRET, naverApiConfig.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(content, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(naverApiConfig.getUrl(), requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        boolean isPositive = true;

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);

            String sentiment = rootNode.path("document").path("sentiment").asText();
            if (sentiment.equals("negative")) {
                isPositive = false;
                double documentNegativeConfidence = rootNode.path("document").path("confidence").path("negative").asDouble();
                if (documentNegativeConfidence >= ConstantUtil.LIMIT_NEGATIVE_CONFIDENCE) { // 부정이며 확률이 99.5% 이상일 경우 재작성 요청
                    throw new CommentException(ErrorCode.INVALID_COMMENT);
                }
            }
        } catch (JsonProcessingException e) {
            throw new CommentException(ErrorCode.INVALID_JSON);
        }
        return isPositive;
    }
}
