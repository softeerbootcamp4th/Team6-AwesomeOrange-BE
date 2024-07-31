package hyundai.softeer.orange.eventuser.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "cool-sms")
public class CoolSmsApiConfig {
    private String apiKey;
    private String apiSecret;
    private String from;
    private String url;
}
