package hyundai.softeer.orange.event.dto.draw;

import hyundai.softeer.orange.event.draw.enums.DrawEventAction;
import hyundai.softeer.orange.event.dto.group.EventEditGroup;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DrawEventScorePolicyDto {
    @NotNull(groups = {EventEditGroup.class})
    private Long id;

    @NotNull
    private DrawEventAction action;

    @NotNull
    private Integer score;
}
