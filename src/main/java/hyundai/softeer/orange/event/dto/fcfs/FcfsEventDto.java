package hyundai.softeer.orange.event.dto.fcfs;

import hyundai.softeer.orange.event.dto.group.EventCreateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FcfsEventDto {
    @NotNull(groups = {EventCreateGroup.class})
    private Long id;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    private Long participantCount;

    @NotBlank
    private String prizeInfo;
}
