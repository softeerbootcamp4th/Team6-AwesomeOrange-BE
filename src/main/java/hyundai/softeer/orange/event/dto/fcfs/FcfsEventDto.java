package hyundai.softeer.orange.event.dto.fcfs;

import hyundai.softeer.orange.event.dto.group.EventEditGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class FcfsEventDto {
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