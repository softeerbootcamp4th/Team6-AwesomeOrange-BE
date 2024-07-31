package hyundai.softeer.orange.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AdminSignInRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
