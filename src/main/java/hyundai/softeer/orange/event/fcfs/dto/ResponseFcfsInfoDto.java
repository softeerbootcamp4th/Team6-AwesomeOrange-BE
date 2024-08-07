package hyundai.softeer.orange.event.fcfs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseFcfsInfoDto {

    private LocalDateTime nowDateTime;
    private String eventStatus;
}
