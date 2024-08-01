package hyundai.softeer.orange.event.dto.draw;

import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class DrawEventDto {

    @NotNull(groups = {EventEditGroup.class})
    private Long id;

    @NotNull
    @Valid
    private List<DrawEventScorePolicyDto> policies;

    @NotNull
    @Valid
    private List<DrawEventMetadataDto> metadata;
}
