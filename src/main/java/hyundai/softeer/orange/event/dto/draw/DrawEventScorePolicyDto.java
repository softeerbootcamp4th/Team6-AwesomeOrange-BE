package hyundai.softeer.orange.event.dto.draw;

import hyundai.softeer.orange.event.draw.enums.DrawEventAction;
import hyundai.softeer.orange.event.dto.group.EventEditGroup;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DrawEventScorePolicyDto {
    private Long id;

    @NotNull
    private DrawEventAction action;

    @NotNull
    private Integer score;
}
