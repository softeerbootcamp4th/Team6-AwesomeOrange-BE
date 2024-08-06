package hyundai.softeer.orange.event.dto.draw;

import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 추첨 이벤트 등수에 따른 당첨 인원 및 상품 정보를 표현하는 객체
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DrawEventMetadataDto {
    /**
     * 추첨 이벤트 메타데이터에 대한 id 값. 서버 내부적으로 사용
     */
    private Long id;

    /**
     * 추첨 이벤트 등수
     */
    @NotNull
    private Long grade;

    /**
     * 현재 등수에 대한 최대 당첨 인원 수
     */
    @NotNull
    private Long count;

    /**
     * 상품 정보를 기입하는 영역
     */
    @NotNull
    private String prizeInfo;
}
