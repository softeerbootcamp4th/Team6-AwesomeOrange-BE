package hyundai.softeer.orange.event.dto.draw;

import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * 추첨 이벤트를 나타내는 객체
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DrawEventDto {
    /**
     * 추첨 이벤트의 id 값. 서버 내부적으로 사용하는 데이터.
     */
    private Long id;

    @NotNull
    @Valid
    private List<DrawEventScorePolicyDto> policies;

    @NotNull
    @Valid
    private List<DrawEventMetadataDto> metadata;
}
