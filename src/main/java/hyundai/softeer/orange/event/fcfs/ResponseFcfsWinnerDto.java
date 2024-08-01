package hyundai.softeer.orange.event.fcfs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ResponseFcfsWinnerDto {

    private String name;
    private String phoneNumber;
}
