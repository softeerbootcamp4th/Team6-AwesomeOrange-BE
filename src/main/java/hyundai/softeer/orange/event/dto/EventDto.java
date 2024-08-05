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

    @NotNull(groups = {EventEditGroup.class})
    String eventId;

    @Size(min = 1, max = 40)
    private String name;
    @Size(min = 1, max = 100)
    private String description;
    @NotNull
    private LocalDateTime startTime;
    @NotNull
    private LocalDateTime endTime;
    @NotBlank
    private String url;

    @NotNull
    private EventType eventType;

    /**
     * 이벤트에 대한 태그 ex) 2024 현대 여름 이벤트
     */
    @NotNull
    private String tag;

    @Valid
    private List<FcfsEventDto> fcfs;

    @Valid
    private DrawEventDto draw;

    public void setDraw(DrawEventDto drawEventDto) {
        this.draw = drawEventDto;
    }

    public void setFcfsList(List<FcfsEventDto> fcfsEventDtos) {
        this.fcfs = fcfsEventDtos;
    }
}