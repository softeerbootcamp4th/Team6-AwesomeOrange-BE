package hyundai.softeer.orange.event.dto.draw;

import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DrawEventDto {
    private Long id;

    @NotNull
    @Valid
    private List<DrawEventScorePolicyDto> policies;

    @NotNull
    @Valid
    private List<DrawEventMetadataDto> metadata;
}
