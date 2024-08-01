package hyundai.softeer.orange.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EventFrameCreateRequest {
    @NotBlank
    private String name;
}
