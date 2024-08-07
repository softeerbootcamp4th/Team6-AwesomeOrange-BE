package hyundai.softeer.orange.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // LocalDateTime파싱이 목적
        objectMapper.registerModule(new JavaTimeModule());
        // timestamp를 문자열로 전달
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // serialization 시 값 없는 필드 = null을 노출하지 않도록 제외. 문제가 되는 경우 구체적인 dto로 이동할 예정.
        // 참고: https://www.baeldung.com/jackson-ignore-null-fields
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }
}
