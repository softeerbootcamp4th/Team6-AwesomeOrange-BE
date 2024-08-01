package hyundai.softeer.orange.event.dto;

import hyundai.softeer.orange.event.dto.draw.DrawEventDto;
import hyundai.softeer.orange.event.dto.fcfs.FcfsEventDto;
import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import hyundai.softeer.orange.event.common.enums.EventType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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

    @Valid
    private List<FcfsEventDto> fcfs;

    @Valid
    private DrawEventDto draw;
}