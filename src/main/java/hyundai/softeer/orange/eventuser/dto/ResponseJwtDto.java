package hyundai.softeer.orange.eventuser.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Getter
public class ResponseJwtDto {

    private String accessToken;
    private String refreshToken;

    public ResponseJwtDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
