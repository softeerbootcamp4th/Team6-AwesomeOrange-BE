package hyundai.softeer.orange.event.url.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ResponseUrlDto {
    private String shortUrl;

    public ResponseUrlDto(String shortUrl) {
        this.shortUrl = shortUrl;
    }
}
