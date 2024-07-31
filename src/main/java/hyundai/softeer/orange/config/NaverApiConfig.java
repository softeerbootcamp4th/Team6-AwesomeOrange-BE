package hyundai.softeer.orange.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "naver")
public class NaverApiConfig {
    private String clientId;
    private String clientSecret;
    private String url;
}
