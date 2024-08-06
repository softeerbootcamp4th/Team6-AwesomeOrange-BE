package hyundai.softeer.orange.event.dto;

import hyundai.softeer.orange.event.dto.draw.DrawEventDto;
import hyundai.softeer.orange.event.dto.fcfs.FcfsEventDto;
import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import hyundai.softeer.orange.event.common.enums.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventDto {
    @NotNull(groups = {EventEditGroup.class})
    Long id;

    /**
     * HD000000_000 형식으로 구성된 id 값
     */
    @NotNull(groups = {EventEditGroup.class})
    String eventId;

    /**
     * 이벤트의 이름
     */
    @Size(min = 1, max = 40)
    private String name;

    /**
     * 이벤트에 대한 설명
     */
    @Size(min = 1, max = 100)
    private String description;

    /**
     * 이벤트 시작 시간
     */
    @NotNull
    private LocalDateTime startTime;

    /**
     * 이벤트 종료 시간
     */
    @NotNull
    private LocalDateTime endTime;

    /**
     * 이벤트 페이지의 url
     */
    @NotBlank
    private String url;


    /**
     * 이벤트의 타입
     */
    @NotNull
    private EventType eventType;

    /**
     * 이벤트 프레임 정보. 추후 변경될 수 있음.
     */
    @NotNull
    private String tag;

    /**
     * fcfs 이벤트 내용을 정의하는 부분
     */
    @Valid
    private List<FcfsEventDto> fcfs;

    /**
     * draw 이벤트 내용을 정의하는 부분
     */
    @Valid
    private DrawEventDto draw;

    public void setDraw(DrawEventDto drawEventDto) {
        this.draw = drawEventDto;
    }

    public void setFcfsList(List<FcfsEventDto> fcfsEventDtos) {
        this.fcfs = fcfsEventDtos;
    }
}