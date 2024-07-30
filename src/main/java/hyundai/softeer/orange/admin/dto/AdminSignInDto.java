package hyundai.softeer.orange.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AdminSignInDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
