package hyundai.softeer.orange.event.dto.draw;

import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DrawEventMetadataDto {
    private Long id;

    @NotNull
    private Long grade;

    @NotNull
    private Long count;

    @NotNull
    private String prizeInfo;
}
